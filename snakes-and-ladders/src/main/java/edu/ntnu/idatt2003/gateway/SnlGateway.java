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
import edu.games.engine.rule.factory.RuleFactory;
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

public final class SnlGateway extends AbstractGameGateway {
  private final JsonBoardLoader boardFactory;
  private final DiceFactory diceFactory;
  private final RuleFactory ruleFactory;
  private GameStrategy gameStrategy;

  public SnlGateway(
      JsonBoardLoader boardFactory,
      RuleFactory ruleFactory,
      DiceFactory diceFactory,
      PlayerStore playerStore,
      OverlayProvider overlayProvider) {
    super(playerStore, overlayProvider);
    this.boardFactory = boardFactory;
    this.ruleFactory = ruleFactory;
    this.diceFactory = diceFactory;
  }

  @Override
  public void newGame(int size) {
    String resource = "/boards/board" + size + ".json";
    BoardAdapter.MapData map = BoardFactory.loadFromClasspath(resource);
    this.gameStrategy = GameStrategyFactory.createSnlStrategy(map);
    Board board = boardFactory.create(map.size());
    Dice dice = diceFactory.create();
    game = new DefaultGame(board, gameStrategy, new ArrayList<>(), dice);

    gameStrategy.initializeGame(game);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_STARTED, size));
  }

  @Override
  public void newGame(BoardAdapter.MapData data) {
    LinearBoard board = new LinearBoard(data.size());
    Dice dice = new RandomDice(2);
    game = new DefaultGame(board, gameStrategy, new ArrayList<>(), dice);
  }

  @Override
  public void resetGame() {
    if (game == null) return;
    game.players().forEach(p -> p.moveTo(game.board().start()));
    game.setWinner(null);
    game.setCurrentPlayerIndex(0);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
  }

  @Override
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game, "call newGame first");
    Player newPlayer = new Player(name, mapStringToToken(token), birthday);
    newPlayer.moveTo(game.board().start()); // Set player on first tile
    game.players().add(newPlayer);

    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYER_ADDED, newPlayer));
  }

  @Override
  public int rollDice() {
    if (game == null || game.players().isEmpty()) return 0;

    // Save current player info for notifications
    Player currentPlayer = game.currentPlayer();
    int startPosition = currentPlayer.getCurrentTile().tileId();

    // Use DefaultGame.playTurn() to handle the turn logic
    int rollValue = game.playTurn();
    lastDiceValues = game.dice().lastValues();

    // Send notifications as needed
    notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.DICE_ROLLED, lastDiceValues));

    // Only send move notification if player actually moved
    if (currentPlayer.getCurrentTile().tileId() != startPosition) {
      notifyObservers(
          new BoardGameEvent(
              BoardGameEvent.EventType.PLAYER_MOVED,
              new PlayerMoveData(
                  currentPlayer, startPosition, currentPlayer.getCurrentTile().tileId())));
    }

    // Check for winner
    if (game.winner().isPresent()) {
      notifyObservers(
          new BoardGameEvent(BoardGameEvent.EventType.WINNER_DECLARED, game.winner().get()));
    }

    // Notify about current player (might have changed)
    notifyObservers(
        new BoardGameEvent(BoardGameEvent.EventType.TURN_CHANGED, game.currentPlayer()));

    return rollValue;
  }

  @Override
  public int boardSize() {
    if (game == null) return 0;
    LinearBoard linearBoard = (LinearBoard) game.board();
    return ((LinearTile) linearBoard.move(linearBoard.start(), Integer.MAX_VALUE)).tileId();
  }

  @Override
  public List<PlayerView> players() {
    if (game == null || game.players().isEmpty()) {
      return List.of();
    }

    Token turnToken = game.currentPlayer().getToken();

    return game.players().stream()
        .map(
            p ->
                new PlayerView(
                    p.getName(),
                    p.getToken().name(),
                    p.getCurrentTile().tileId(),
                    p.getBirtday(),
                    p.getToken() == turnToken))
        .toList();
  }

  @Override
  protected Token mapStringToToken(String token) {
    return TokenMapperFactory.getSnlToken(token);
  }

  public Map<Integer, Integer> getSnakes() {
    if (gameStrategy instanceof SnlGameStrategy strategy) {
      return strategy.getSnakes();
    }
    return Map.of();
  }

  public Map<Integer, Integer> getLadders() {
    if (gameStrategy instanceof SnlGameStrategy strategy) {
      return strategy.getLadders();
    }

    return Map.of();
  }
}
