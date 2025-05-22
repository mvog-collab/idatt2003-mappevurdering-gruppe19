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

public final class LudoGateway extends AbstractGameGateway {

  // Fields
  private final DiceFactory diceFactory;
  private int selectedPieceIndex = -1;
  private Player winner = null;

  // Construction
  public LudoGateway(
      DiceFactory diceFactory, PlayerStore playerStore, OverlayProvider overlayProvider) {
    super(playerStore, overlayProvider);
    this.diceFactory = diceFactory;
  }

  public static LudoGateway createDefault() {
    return new LudoGateway(
        new edu.games.engine.dice.factory.RandomDiceFactory(1),
        new edu.games.engine.impl.CsvPlayerStore(),
        new edu.games.engine.impl.overlay.JsonOverlayProvider("/overlays/"));
  }

  // Game lifecycle
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

  @Override
  public void newGame(MapData data) {
    newGame(0); // Ludo ignores custom board data for now
  }

  @Override
  public void resetGame() {
    if (game == null)
      return;

    game.players().forEach(p -> p.getPieces().forEach(piece -> piece.moveTo(null)));
    game.setCurrentPlayerIndex(0);
    winner = null;

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
  }

  // Player management
  @Override
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game, "Call newGame before adding players");
    Player player = new Player(name, mapStringToToken(token), birthday);
    game.players().add(player);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_ADDED, player));
  }

  // UI interaction helpers
  public void selectPiece(int pieceIndex) {
    if (game == null || game.players().isEmpty()) {
      return;
    }

    if (pieceIndex < 0 || pieceIndex >= 4) {
      throw new ValidationException("pieceIndex out of range: " + pieceIndex);
    }

    selectedPieceIndex = pieceIndex;
    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.PIECE_SELECTED, selectedPieceIndex));
  }

  // Gameplay – public entry points
  @Override
  public int rollDice() {
    if (game == null || game.players().isEmpty() || winner != null)
      return 0;
    return performDiceRoll();
  }

  public int applyPieceMovement() {
    if (game == null || game.players().isEmpty() || winner != null || selectedPieceIndex < 0) {
      return 0;
    }
    int roll = lastDiceValues.get(0);
    moveSelectedPiece(roll);
    return roll;
  }

  // Gameplay – internal helpers
  private int performDiceRoll() {
    final int roll = game.dice().roll();
    lastDiceValues = game.dice().lastValues();
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.DICE_ROLLED, lastDiceValues));

    Log.game().info(() -> "%s rolled %s".formatted(game.currentPlayer().getName(), lastDiceValues));
    return roll;
  }

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

  private Tile calculateDestination(Player player, int roll) {
    return game.getStrategy().movePiece(player, selectedPieceIndex, roll, game);
  }

  private boolean hasMoved(Tile from, Tile to) {
    if (to == null)
      return false;
    if (from == null)
      return true;
    return to.id() != from.id();
  }

  private void executeMove(PlayerPiece piece, Tile destination) {
    if (game != null && game.players() != null) {
      for (Player p : game.players()) {
        if (p.getPieces().contains(piece)) {
          break;
        }
      }
    }
    piece.moveTo(destination);
  }

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

  private void declareWinnerIfAny(Player player) {
    if (game.getStrategy().checkWinCondition(player, game)) {
      winner = player;
      notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.WINNER_DECLARED, winner));
    }
  }

  private void advanceTurnIfNoExtraTurn(Player player, int roll) {
    boolean extraTurn = game.getStrategy().processDiceRoll(player, roll, game);
    if (!extraTurn)
      passTurnToNextPlayer(player);
  }

  private void passTurnToNextPlayer(Player current) {
    int nextIdx = (game.players().indexOf(current) + 1) % game.players().size();
    game.setCurrentPlayerIndex(nextIdx);
  }

  private void notifyTurnChanged() {
    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.TURN_CHANGED, game.currentPlayer()));
  }

  private void resetSelection() {
    selectedPieceIndex = -1;
  }

  @Override
  public boolean hasWinner() {
    return winner != null;
  }

  @Override
  public int boardSize() {
    return 57; // fixed board length for classic Ludo
  }

  @Override
  public List<PlayerView> players() {
    if (game == null || game.players().isEmpty())
      return List.of();

    Token turnToken = game.currentPlayer().getToken();
    return game.players().stream().map(p -> mapToView(p, turnToken)).toList();
  }

  private PlayerView mapToView(Player p, Token turnToken) {
    List<Integer> positions = p.getPieces().stream()
        .map(
            piece -> {
              return piece.getCurrentTile() == null ? 0 : piece.getCurrentTile().id();
            })
        .toList();
    int activeIndex = p.getToken() == turnToken ? selectedPieceIndex : -1;
    return new PlayerView(
        p.getName(),
        p.getToken().name(),
        positions,
        p.getBirtday(),
        p.getToken() == turnToken,
        activeIndex);
  }

  @Override
  protected Token mapStringToToken(String token) {
    return TokenMapperFactory.getLudoToken(token);
  }
}
