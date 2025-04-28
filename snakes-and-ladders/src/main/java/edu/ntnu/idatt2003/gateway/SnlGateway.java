package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import edu.games.engine.*;
import edu.ntnu.idatt2003.ui.OverlayParams;
import edu.ntnu.idatt2003.utils.CsvPlayerHandler;
import edu.ntnu.idatt2003.utils.ResourcePaths;

public final class SnlGateway implements GameGateway {

  private DefaultGame game;

  private final Map<String, Token> tokenMap = Map.of(
          "BLUE",Token.BLUE, "GREEN",Token.GREEN, "YELLOW",Token.YELLOW,
          "RED",Token.RED,   "PURPLE",Token.PURPLE);

  private final CsvPlayerHandler csv = new CsvPlayerHandler();

  /* ---------- set-up ---------- */

  @Override 
  public void newGame(int boardSize) {
    LinearBoard board = new LinearBoard(boardSize);
    RuleEngine ruleEngine = new SnlRuleEngine(snakes(boardSize), ladders(boardSize));
    game = new DefaultGame(board, ruleEngine, new ArrayList<>(), 2);
  }

  @Override 
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game,"call newGame first");
    game.players().add(new Player(name, tokenMap.get(token), birthday));
  }

  @Override 
  public void loadPlayers(List<String[]> rows) {
    rows.forEach(arr -> addPlayer(arr[0], arr[1], LocalDate.parse(arr[2])));
  }

  @Override 
  public void savePlayers(Path out) throws IOException {
    List<edu.ntnu.idatt2003.models.Player> tmp =
        game.players().stream()
            .map(p -> new edu.ntnu.idatt2003.models.Player(
                    p.getName(),
                    edu.ntnu.idatt2003.models.PlayerTokens.valueOf(p.getToken().name()),
                    p.getBirtday()))
            .toList();
    csv.save(tmp, out);
  }

  /* ---------- play ---------- */

  @Override 
  public int rollDice() { 
    return game.playTurn(); 
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

  @Override 
  public List<OverlayParams> boardOverlays() {
    // existing util already reads JSON into OverlayParams
    return new edu.ntnu.idatt2003.ui.BoardView(null).loadOverlaysFromJson();
  }

  @Override 
  public List<PlayerView> players() {
    Token turnToken = game.currentPlayer().getToken();
    return game.players().stream()
          .map(p -> new PlayerView(p.getName(),
                                   p.getToken().name(),
                                   p.getCurrentTile().id(),
                                   p.getToken()==turnToken))
          .collect(Collectors.toList());
  }

  /* ---------- static helpers ---------- */

  private static Map<Integer,Integer> snakes(int size) {          // tiny demo
    return size==64 ? Map.of(47,26, 62,18)
         :            Map.of(87,24, 73,15);
  }

  private static Map<Integer,Integer> ladders(int size) {
    return size==64 ? Map.of(4,14, 22,33)
         :            Map.of(11,29, 40,68);
  }
}