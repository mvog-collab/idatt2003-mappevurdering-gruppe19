package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import edu.games.engine.board.LinearTile;
import edu.games.engine.board.factory.JsonBoardLoader;
import edu.games.engine.dice.Dice;
import edu.games.engine.dice.RandomDice;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.store.PlayerStore;
import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.games.engine.strategy.factory.GameStrategyFactory;
import edu.ntnu.idatt2003.gateway.event.PlayerMoveData;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import edu.ntnu.idatt2003.persistence.BoardFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Gateway for a Snakes-and-Ladders game, mediating between the UI and the game engine.
 * <p>
 * Responsible for initializing, resetting, and driving game play,
 * notifying observers of events such as dice rolls, moves, and game start/reset.
 */
public final class SnlGateway extends AbstractGameGateway {
  private final JsonBoardLoader boardFactory;
  private final DiceFactory diceFactory;
  private GameStrategy gameStrategy;

  /**
   * Constructs a new SnlGateway with the given dependencies.
   *
   * @param boardFactory    loader for board configurations
   * @param diceFactory     factory to create dice instances
   * @param playerStore     persistence for player data
   * @param overlayProvider provider for UI overlays
   */
  public SnlGateway(
      JsonBoardLoader boardFactory,
      DiceFactory diceFactory,
      PlayerStore playerStore,
      OverlayProvider overlayProvider) {
    super(playerStore, overlayProvider);
    this.boardFactory = boardFactory;
    this.diceFactory = diceFactory;
  }

  /**
   * Starts a new game using a JSON board of the given size.
   * Loads board data, strategy, and initializes players list.
   *
   * @param size the size identifier for the board JSON file
   */
  @Override
  public void newGame(int size) {
    String resource = "/boards/board" + size + ".json";
    BoardAdapter.MapData map = BoardFactory.loadFromClasspath(resource);
    this.gameStrategy = GameStrategyFactory.createSnlStrategy(map);
    Board board = boardFactory.create(map.boardSize());
    Dice dice = diceFactory.create();
    game = new DefaultGame(board, gameStrategy, new ArrayList<>(), dice);

    gameStrategy.initializeGame(game);
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_STARTED, size));
  }

  /**
   * Starts a new game from pre-loaded MapData.
   * Uses a default linear board and two-dice random roller.
   *
   * @param data the map data (ignored for board content)
   */
  @Override
  public void newGame(BoardAdapter.MapData data) {
    LinearBoard board = new LinearBoard(data.boardSize());
    Dice dice = new RandomDice(2);
    game = new DefaultGame(board, gameStrategy, new ArrayList<>(), dice);
  }

  /**
   * Resets the current game to its initial tile positions
   * and notifies observers of the reset.
   */
  @Override
  public void resetGame() {
    if (game == null) return;
    game.getPlayers().forEach(p -> p.moveTo(game.getBoard().start()));
    game.setWinner(null);
    game.setCurrentPlayerIndex(0);
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
  }

  /**
   * Adds a new player and places them on the start tile.
   *
   * @param playerName  the name of the player
   * @param playerToken the token identifier string
   * @param birthday    the player's birth date
   */
  @Override
  public void addPlayer(String playerName, String playerToken, LocalDate birthday) {
    Objects.requireNonNull(game, "call newGame first");
    Player newPlayer = new Player(playerName, mapStringToToken(playerToken), birthday);
    newPlayer.moveTo(game.getBoard().start());
    game.getPlayers().add(newPlayer);
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_ADDED, newPlayer));
  }

  /**
   * Rolls the dice and advances the game turn,
   * notifying observers of dice roll, move, and potential win.
   *
   * @return the total value rolled
   */
  @Override
  public int rollDice() {
    if (game == null || game.getPlayers().isEmpty()) return 0;
    Player currentPlayer = game.currentPlayer();
    int startPosition = currentPlayer.getCurrentTile().tileId();

    int rollValue = game.playTurn();
    lastDiceValues = game.getDice().lastValues();
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.DICE_ROLLED, lastDiceValues));

    if (currentPlayer.getCurrentTile().tileId() != startPosition) {
      notifyObservers(new BoardGameEvent(
          BoardGameEvent.EventType.PLAYER_MOVED,
          new PlayerMoveData(
              currentPlayer,
              startPosition,
              currentPlayer.getCurrentTile().tileId())));
    }

    game.getWinner().ifPresent(w ->
        notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.WINNER_DECLARED, w)));

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.TURN_CHANGED, game.currentPlayer()));
    return rollValue;
  }

  /**
   * Returns the highest tile ID reachable on the current board.
   *
   * @return the board's maximum tile ID
   */
  @Override
  public int boardSize() {
    if (game == null) return 0;
    LinearBoard linearBoard = (LinearBoard) game.getBoard();
    return ((LinearTile) linearBoard.move(linearBoard.start(), Integer.MAX_VALUE)).tileId();
  }

  /**
   * Returns a list of player views reflecting the current game state.
   *
   * @return list of {@link PlayerView} objects
   */
  @Override
  public List<PlayerView> players() {
    if (game == null || game.getPlayers().isEmpty()) {
      return List.of();
    }
    Token turnToken = game.currentPlayer().getToken();
    return game.getPlayers().stream()
        .map(p -> new PlayerView(
            p.getName(),
            p.getToken().name(),
            p.getCurrentTile().tileId(),
            p.getBirthday(),
            p.getToken() == turnToken))
        .toList();
  }

  /**
   * Maps a token string to the {@link Token} enum specific to this gateway.
   *
   * @param token the token string
   * @return the corresponding {@link Token}
   */
  @Override
  protected Token mapStringToToken(String token) {
    return TokenMapperFactory.getSnlToken(token);
  }

  /**
   * Exposes the snake positions for UI display.
   *
   * @return unmodifiable map of snake start->end positions
   */
  public Map<Integer, Integer> getSnakes() {
    if (gameStrategy instanceof SnlGameStrategy s) {
      return s.getSnakes();
    }
    return Map.of();
  }

  /**
   * Exposes the ladder positions for UI display.
   *
   * @return unmodifiable map of ladder start->end positions
   */
  public Map<Integer, Integer> getLadders() {
    if (gameStrategy instanceof SnlGameStrategy s) {
      return s.getLadders();
    }
    return Map.of();
  }
}
