package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.exception.ValidationException;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.model.Player;
import edu.games.engine.model.PlayerPiece;
import edu.games.engine.model.Token;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.store.PlayerStore;
import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.factory.GameStrategyFactory;
import edu.ntnu.idatt2003.gateway.event.PlayerMoveData;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter.MapData;
import edu.ntnu.idatt2003.utils.Log;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gateway for managing a Ludo game session, bridging between the UI and the game engine.
 * <p>
 * Responsibilities include creating new games, resetting state, handling player addition,
 * rolling dice, selecting and moving pieces, and notifying observers of game events.
 * </p>
 */
public final class LudoGateway extends AbstractGameGateway {

  private final DiceFactory diceFactory;
  private int selectedPieceIndex = -1;
  private Player winner = null;

  /**
   * Constructs a LudoGateway with the given dependencies.
   *
   * @param diceFactory     factory for creating dice instances
   * @param playerStore     storage service for saving and loading players
   * @param overlayProvider provider for board overlay parameters
   */
  public LudoGateway(
      DiceFactory diceFactory, PlayerStore playerStore, OverlayProvider overlayProvider) {
    super(playerStore, overlayProvider);
    this.diceFactory = diceFactory;
  }

  /**
   * Creates a default LudoGateway with standard implementations.
   *
   * @return a new LudoGateway using default factories
   */
  public static LudoGateway createDefault() {
    return new LudoGateway(
        new edu.games.engine.dice.factory.RandomDiceFactory(1),
        new edu.games.engine.impl.CsvPlayerStore(),
        new edu.games.engine.impl.overlay.JsonOverlayProvider("/overlays/"));
  }

  /**
   * Starts a new Ludo game, initializing board, path, strategy, and notifying observers.
   *
   * @param ignored ignored parameter for compatibility
   */
  @Override
  public void newGame(int ignored) {
    LudoPath path = new LudoPath();
    LudoBoard board = new LudoBoard(path);
    GameStrategy strategy = GameStrategyFactory.createLudoStrategy(path);

    game = new DefaultGame(board, strategy, new ArrayList<>(), diceFactory.create());
    winner = null;

    strategy.initializeGame(game);
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_STARTED, 0));
  }

  /**
   * Starts a new Ludo game, ignoring custom MapData.
   *
   * @param data custom map data (ignored for Ludo)
   */
  @Override
  public void newGame(MapData data) {
    newGame(0);
  }

  /**
   * Resets the current game to its initial state and notifies observers.
   */
  @Override
  public void resetGame() {
    if (game == null) {
      return;
    }

    game.getPlayers().forEach(p -> p.getPieces().forEach(piece -> piece.moveTo(null)));
    game.setCurrentPlayerIndex(0);
    winner = null;

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
  }

  /**
   * Adds a new player to the game and notifies observers.
   *
   * @param playerName  the name of the player
   * @param playerToken the token string representing their color
   * @param birthday    the player's birthday
   * @throws NullPointerException     if game is not initialized
   * @throws ValidationException      if token mapping fails
   */
  @Override
  public void addPlayer(String playerName, String playerToken, LocalDate birthday) {
    Objects.requireNonNull(game, "Call newGame before adding players");
    Player player = new Player(playerName, mapStringToToken(playerToken), birthday);
    game.getPlayers().add(player);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_ADDED, player));
  }

  /**
   * Selects a piece index for the current player and notifies observers.
   *
   * @param pieceIndex index of the piece (0–3)
   * @throws ValidationException if index is out of range
   */
  public void selectPiece(int pieceIndex) {
    if (game == null || game.getPlayers().isEmpty()) {
      return;
    }
    if (pieceIndex < 0 || pieceIndex >= 4) {
      throw new ValidationException("pieceIndex out of range: " + pieceIndex);
    }
    selectedPieceIndex = pieceIndex;
    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.PIECE_SELECTED, selectedPieceIndex));
  }

  /**
   * Rolls the dice for the current turn.
   *
   * @return the total roll value, or 0 if roll is not allowed
   */
  @Override
  public int rollDice() {
    if (game == null || game.getPlayers().isEmpty() || winner != null) {
      return 0;
    }
    return performDiceRoll();
  }

  /**
   * Moves the previously selected piece according to last roll.
   *
   * @return the roll value used for movement, or 0 if move is not allowed
   */
  public int applyPieceMovement() {
    if (game == null || game.getPlayers().isEmpty() || winner != null || selectedPieceIndex < 0) {
      return 0;
    }
    int roll = lastDiceValues.get(0);
    moveSelectedPiece(roll);
    return roll;
  }

  /** Performs the dice roll and notifies observers of the result. */
  private int performDiceRoll() {
    final int roll = game.getDice().roll();
    lastDiceValues = game.getDice().lastValues();
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.DICE_ROLLED, lastDiceValues));
    Log.game().info(() -> "%s rolled %s".formatted(game.currentPlayer().getName(), lastDiceValues));
    return roll;
  }

  /**
   * Executes the process of moving the chosen piece:
   * calculates destination, validates move, executes movement,
   * applies post-move rules, and advances or retains turn.
   *
   * @param roll the dice roll value
   */
  private void moveSelectedPiece(int roll) {
    Player player = game.currentPlayer();
    PlayerPiece piece = player.getPiece(selectedPieceIndex);
    Tile from = piece.getCurrentTile();
    Tile to = calculateDestination(player, roll);

    if (!hasMoved(from, to)) {
      passTurnToNextPlayer(player);
      notifyTurnChanged();
      resetSelection();
      return;
    }

    executeMove(piece, to);
    postMoveProcessing(player, piece, from, to, roll);
    resetSelection();
  }

  /** Determines the destination tile for a move. */
  private Tile calculateDestination(Player player, int roll) {
    return game.getStrategy().movePiece(player, selectedPieceIndex, roll, game);
  }

  /** Checks if a move actually changes the tile position. */
  private boolean hasMoved(Tile from, Tile to) {
    if (to == null) {
      return false;
    }
    if (from == null) {
      return true;
    }
    return to.tileId() != from.tileId();
  }

  /** Applies the tile change to the piece. */
  private void executeMove(PlayerPiece piece, Tile destination) {
    piece.moveTo(destination);
  }

  /**
   * Handles post-move logic: special rules, extra turn, winner declaration,
   * notification of move and turn change.
   */
  private void postMoveProcessing(Player player, PlayerPiece piece, Tile from, Tile to, int roll) {
    GameStrategy strategy = game.getStrategy();
    strategy.applySpecialRules(player, piece, to, game);
    strategy.processDiceRoll(player, roll, game); // may grant extra turn
    declareWinnerIfAny(player);

    notifyObservers(
        new BoardGameEvent(
            BoardGameEvent.EventType.PLAYER_MOVED, new PlayerMoveData(player, from, to)));

    advanceTurnIfNoExtraTurn(player, roll);
    notifyTurnChanged();
  }

  /** Declares a winner and notifies observers if the player has won. */
  private void declareWinnerIfAny(Player player) {
    if (game.getStrategy().checkWinCondition(player, game)) {
      winner = player;
      notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.WINNER_DECLARED, winner));
    }
  }

  /** Advances the turn if no extra turn was granted by the rules. */
  private void advanceTurnIfNoExtraTurn(Player player, int roll) {
    boolean extraTurn = game.getStrategy().processDiceRoll(player, roll, game);
    if (!extraTurn) {
      passTurnToNextPlayer(player);
    }
  }

  /** Moves the current index to the next player. */
  private void passTurnToNextPlayer(Player current) {
    int nextIdx = (game.getPlayers().indexOf(current) + 1) % game.getPlayers().size();
    game.setCurrentPlayerIndex(nextIdx);
  }

  /** Notifies observers that the turn has changed. */
  private void notifyTurnChanged() {
    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.TURN_CHANGED, game.currentPlayer()));
  }

  /** Resets the selected piece index for the next action. */
  private void resetSelection() {
    selectedPieceIndex = -1;
  }

  /**
   * Checks if the game has a winner.
   *
   * @return true if a winner has been declared, false otherwise
   */
  @Override
  public boolean hasWinner() {
    return winner != null;
  }

  /**
   * Returns the fixed board size for classical Ludo.
   *
   * @return the number of tiles on a Ludo board
   */
  @Override
  public int boardSize() {
    return 57;
  }

  /**
   * Provides a list of PlayerView objects reflecting current players and state.
   *
   * @return list of PlayerView instances, or empty if game not initialized
   */
  @Override
  public List<PlayerView> players() {
    if (game == null || game.getPlayers().isEmpty()) {
      return List.of();
    }
    Token turnToken = game.currentPlayer().getToken();
    return game.getPlayers().stream().map(p -> mapToView(p, turnToken)).toList();
  }

  /** Maps a Player model to a PlayerView DTO. */
  private PlayerView mapToView(Player p, Token turnToken) {
    List<Integer> positions = p.getPieces().stream()
        .map(piece -> piece.getCurrentTile() == null ? 0 : piece.getCurrentTile().tileId())
        .toList();
    int activeIndex = p.getToken() == turnToken ? selectedPieceIndex : -1;
    return new PlayerView(
        p.getName(),
        p.getToken().name(),
        positions,
        p.getBirthday(),
        p.getToken() == turnToken,
        activeIndex);
  }

  /** Translates a token string into a Token enum for Ludo. */
  @Override
  protected Token mapStringToToken(String token) {
    return TokenMapperFactory.getLudoToken(token);
  }
}
