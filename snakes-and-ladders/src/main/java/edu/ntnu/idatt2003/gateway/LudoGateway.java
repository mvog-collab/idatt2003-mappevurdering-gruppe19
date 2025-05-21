package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.LudoBoard;
import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
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
  private final DiceFactory diceFactory;
  private int selectedPieceIndex = -1;
  private Player winner = null;

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

  @Override
  public void newGame(int ignored) {
    LudoPath path = new LudoPath();
    LudoBoard board = new LudoBoard(path);
    GameStrategy ludoStrategy = GameStrategyFactory.createLudoStrategy(path);
    Dice dice = diceFactory.create();
    game = new DefaultGame(board, ludoStrategy, new ArrayList<>(), dice);
    winner = null;

    ludoStrategy.initializeGame(game);

    notifyObservers(
        new BoardGameEvent(
            BoardGameEvent.EventType.GAME_STARTED, 0 // Board size for Ludo
            ));
  }

  @Override
  public void newGame(MapData data) {
    newGame(0);
  }

  @Override
  public void resetGame() {
    if (game == null) return;
    game.players()
        .forEach(
            p -> {
              for (PlayerPiece piece : p.getPieces()) {
                piece.moveTo(null);
              }
            });
    game.setCurrentPlayerIndex(0);
    winner = null;

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
  }

  @Override
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game, "Call newGame before adding players");
    Player player = new Player(name, mapStringToToken(token), birthday);
    game.players().add(player);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_ADDED, player));
  }

  public void selectPiece(int pieceIndex) {
    if (game == null || game.players().isEmpty()) return;

    if (pieceIndex < 0 || pieceIndex >= 4) {
      throw new ValidationException("pieceIndex out of range: " + pieceIndex);
    }

    selectedPieceIndex = pieceIndex;

    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.PIECE_SELECTED, selectedPieceIndex));
  }

  @Override
  public int rollDice() {
    if (game == null || game.players().isEmpty() || winner != null) return 0;

    int rollValue = game.dice().roll();
    lastDiceValues = game.dice().lastValues();

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.DICE_ROLLED, lastDiceValues));

    Player currentPlayer = game.currentPlayer();
    Log.game().info(() -> "%s rolled %s".formatted(currentPlayer.getName(), lastDiceValues));

    if (selectedPieceIndex < 0) {
      return rollValue;
    }

    return handlePieceMovement(currentPlayer, rollValue);
  }

  private int handlePieceMovement(Player currentPlayer, int rollValue) {
    PlayerPiece selectedPiece = currentPlayer.getPiece(selectedPieceIndex);
    Tile initialTile = selectedPiece.getCurrentTile();

    GameStrategy gameStrategy = game.getStrategy();

    // Use strategy to determine destination
    Tile destinationTile =
        gameStrategy.movePiece(currentPlayer, selectedPieceIndex, rollValue, game);

    if (destinationTile != null && destinationTile != initialTile) {
      // Move the piece
      selectedPiece.moveTo(destinationTile);

      // Apply special rules
      gameStrategy.applySpecialRules(currentPlayer, selectedPiece, destinationTile, game);

      // Check if this player has won
      if (gameStrategy.checkWinCondition(currentPlayer, game)) {
        winner = currentPlayer;
        notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.WINNER_DECLARED, winner));
      }

      // Determine if player gets another turn
      boolean extraTurn = gameStrategy.processDiceRoll(currentPlayer, rollValue, game);

      System.out.println("Player rolled " + rollValue + ", extraTurn=" + extraTurn); // Debug

      if (!extraTurn) {
        int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
        game.setCurrentPlayerIndex(nextIndex);
        System.out.println("Turn passed to player at index " + nextIndex); // Debug
      } else {
        System.out.println("Player gets an extra turn"); // Debug
      }

      // Notify that player moved
      notifyObservers(
          new BoardGameEvent(
              BoardGameEvent.EventType.PLAYER_MOVED,
              new PlayerMoveData(currentPlayer, initialTile, destinationTile)));
    } else {
      // No valid move - move to next player
      int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
      game.setCurrentPlayerIndex(nextIndex);
      System.out.println("No valid move, turn passed to player at index " + nextIndex); // Debug
    }

    // IMPORTANT: The current player might have changed, so we need to get it again
    Player newCurrentPlayer = game.currentPlayer();

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.TURN_CHANGED, newCurrentPlayer));

    // Reset the selected piece
    selectedPieceIndex = -1;

    return rollValue;
  }

  public int applyPieceMovement() {
    if (game == null || game.players().isEmpty() || winner != null || selectedPieceIndex < 0)
      return 0;

    int rollValue = lastDiceValues.get(0);
    Player currentPlayer = game.currentPlayer();

    return handlePieceMovement(currentPlayer, rollValue);
  }

  private void bumpOtherPieces(PlayerPiece movedPiece, Tile destination) {
    if (destination == null || destination.id() > 52) return;

    Player currentPlayer = game.currentPlayer();

    game.players().stream()
        .filter(p -> p != currentPlayer)
        .forEach(
            player -> {
              player.getPieces().stream()
                  .filter(
                      piece -> piece.isOnBoard() && piece.getCurrentTile().id() == destination.id())
                  .forEach(
                      piece -> {
                        piece.moveTo(null);
                        Log.game()
                            .info(
                                () ->
                                    "%s bumps %s's piece back to home"
                                        .formatted(currentPlayer.getName(), player.getName()));
                      });
            });
  }

  private void checkForWinner(Player player) {
    boolean allInGoal =
        player.getPieces().stream()
            .filter(PlayerPiece::isOnBoard)
            .allMatch(
                piece -> piece.getCurrentTile().id() > 52 && piece.getCurrentTile().id() < 77);

    if (allInGoal && player.getPieces().stream().allMatch(PlayerPiece::isOnBoard)) {
      winner = player;
    }
  }

  @Override
  public boolean hasWinner() {
    return winner != null;
  }

  @Override
  public int boardSize() {
    return 57; // Fixed size for Ludo
  }

  @Override
  public List<PlayerView> players() {
    if (game == null || game.players().isEmpty()) return List.of();

    Token turnToken = game.currentPlayer().getToken();

    return game.players().stream()
        .map(
            p -> {
              List<Integer> piecePositions =
                  p.getPieces().stream()
                      .map(
                          piece -> {
                            if (piece.getCurrentTile() == null) {
                              return 0; // 0 means at home
                            } else {
                              return piece.getCurrentTile().id();
                            }
                          })
                      .toList();

              int activePieceIndex = (p.getToken() == turnToken) ? selectedPieceIndex : -1;

              return new PlayerView(
                  p.getName(),
                  p.getToken().name(),
                  piecePositions,
                  p.getBirtday(),
                  p.getToken() == turnToken,
                  activePieceIndex);
            })
        .toList();
  }

  @Override
  protected Token mapStringToToken(String token) {
    return TokenMapperFactory.getLudoToken(token);
  }
}
