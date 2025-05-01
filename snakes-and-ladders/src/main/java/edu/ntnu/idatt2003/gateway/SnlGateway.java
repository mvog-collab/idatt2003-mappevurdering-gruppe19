package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

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
import edu.games.engine.rule.RuleConfig;
import edu.games.engine.rule.RuleEngine;
import edu.games.engine.rule.SnlRuleEngine;
import edu.games.engine.rule.factory.RuleFactory;
import edu.games.engine.store.PlayerStore;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import edu.ntnu.idatt2003.persistence.BoardFactory;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.utils.Log;

public final class SnlGateway implements GameGateway {

  private final JsonBoardLoader boardFactory;
  private final DiceFactory diceFactory;
  private final RuleFactory ruleFactory;
  private final PlayerStore playerStore;
  private final OverlayProvider overlayProvider;
  private final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();
  private final RuleConfig ruleConfig = new RuleConfig();
  private List<Integer> lastDice = List.of(1,1);

  private DefaultGame game;

  private static final Map<String,Token> TOKEN_MAP = Map.of(
        "BLUE",   Token.BLUE,
        "GREEN",  Token.GREEN,
        "YELLOW", Token.YELLOW,
        "RED",    Token.RED,
        "PURPLE", Token.PURPLE);

  /* ---------- set-up ---------- */

  public SnlGateway(JsonBoardLoader boardFactory,
                    RuleFactory  ruleFactory,
                    DiceFactory  diceFactory,
                    PlayerStore  playerStore,
                    OverlayProvider overlayProvider) {
    this.boardFactory   = boardFactory;
    this.ruleFactory    = ruleFactory;
    this.diceFactory    = diceFactory;
    this.playerStore    = playerStore;
    this.overlayProvider = overlayProvider;
  }

  @Override 
  public void newGame(int size) {
    String resource = "/boards/board" + size + ".json";
    BoardAdapter.MapData map = BoardFactory.loadFromClasspath(resource);
    Board      board = boardFactory.create(map.size());
    RuleEngine rules = ruleFactory.create(map, ruleConfig);
    Dice       dice  = diceFactory.create();
    game = new DefaultGame(board, rules, new ArrayList<>(), dice);
  }

  @Override
  public void newGame(BoardAdapter.MapData data) {
    LinearBoard board = new LinearBoard(data.size());
    RuleEngine  rules = new SnlRuleEngine(data.snakes(), data.ladders(), ruleConfig.extraTurn());
    Dice dice = new RandomDice(2);
    game = new DefaultGame(board, rules, new ArrayList<>(), dice);
  }

  @Override
  public void resetGame() {
      if (game == null) return;
      game.winner().ifPresent(w -> {});
      game.players().forEach(p -> p.moveTo(game.board().start()));

      game.setCurrentPlayerIndex(0);
  }

  @Override
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game,"call newGame first");
    Player newPlayer = new Player(name, Objects.requireNonNull(TOKEN_MAP.get(token)), birthday); // add throw for TOKEN_MAP
    newPlayer.moveTo(game.board().start()); // Set player on first tile
    game.players().add(newPlayer);
  }

  @Override 
  public void loadPlayers(List<String[]> rows) {
    rows.forEach(arr -> addPlayer(arr[0], arr[1], LocalDate.parse(arr[2])));
  }

  @Override
  public void savePlayers(Path out) throws IOException {
      playerStore.save(game.players(), out);
  }

  /* ---------- play ---------- */

  @Override
  public int rollDice() {
      int sum = game.playTurn();
      lastDice = game.dice().lastValues();

      Log.game().info(() ->
          "%s rolled %s -> total %d"
          .formatted(currentPlayerName(), lastDice, sum));

      return sum;
  }

  @Override 
  public boolean hasWinner() { 
    return game.winner().isPresent(); 
  }

  @Override 
  public String currentPlayerName() { 
    return game.currentPlayer().getName(); 
  }

  /* ---------- read-only views ---------- */

  @Override 
  public int boardSize() { 
    LinearBoard linearBoard = (LinearBoard) game.board();
    return ((LinearTile) linearBoard.move(linearBoard.start(), Integer.MAX_VALUE)).id();
  }

  private List<OverlayParams> loadOverlays(int size) {
    return overlayProvider.overlaysForBoard(size);
  }

  @Override
  public List<OverlayParams> boardOverlays() {
      int size = boardSize();
      return overlayCache.computeIfAbsent(size, this::loadOverlays);
  }

  @Override
  public List<PlayerView> players() {
    if (game.players().isEmpty()) {
      return List.of();
    }

    Token turnToken = game.currentPlayer().getToken();

    return game.players().stream()
            .map(p -> new PlayerView(p.getName(),
                                    p.getToken().name(),
                                    p.getCurrentTile().id(),
                                    p.getBirtday(),
                                    p.getToken() == turnToken))
            .toList();
  }

  @Override
  public List<Integer> lastDiceValues() {
      return lastDice;
  }
}