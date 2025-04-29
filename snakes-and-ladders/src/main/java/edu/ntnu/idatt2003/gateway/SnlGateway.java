package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.games.engine.*;
import edu.ntnu.idatt2003.ui.OverlayParams;
import edu.ntnu.idatt2003.utils.BoardAdapter;
import edu.ntnu.idatt2003.utils.PlayerCsv;
import edu.ntnu.idatt2003.utils.ResourcePaths;

public final class SnlGateway implements GameGateway {

  private DefaultGame game;

  private final Map<String, Token> tokenMap = Map.of(
          "BLUE",Token.BLUE, "GREEN",Token.GREEN, "YELLOW",Token.YELLOW,
          "RED",Token.RED,   "PURPLE",Token.PURPLE);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();

  /* ---------- set-up ---------- */

  @Override 
  public void newGame(int boardSize) {
    LinearBoard board = new LinearBoard(boardSize);
    RuleEngine ruleEngine = new SnlRuleEngine(snakes(boardSize), ladders(boardSize));
    game = new DefaultGame(board, ruleEngine, new ArrayList<>(), 2);
  }

  @Override
  public void newGame(BoardAdapter.MapData data) {
    LinearBoard board = new LinearBoard(data.size());
    RuleEngine  rules = new SnlRuleEngine(data.snakes(), data.ladders());
    game = new DefaultGame(board, rules, new ArrayList<>(), 2);
}

  @Override
  public void addPlayer(String name, String token, LocalDate birthday) {
    Objects.requireNonNull(game,"call newGame first");
    Player newPlayer = new Player(name, tokenMap.get(token), birthday);
    newPlayer.moveTo(game.board().start()); // Set player on first tile
    game.players().add(newPlayer);
  }

  @Override 
  public void loadPlayers(List<String[]> rows) {
    rows.forEach(arr -> addPlayer(arr[0], arr[1], LocalDate.parse(arr[2])));
  }

  @Override 
  public void savePlayers(Path out) throws IOException {
    List<String[]> rows = game.players().stream()
        .map(p -> new String[]{ p.getName(),
                                p.getToken().name(),
                                p.getBirtday().toString() })
        .toList();
    PlayerCsv.save(rows, out);
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

  /* ---------- static helpers ---------- */

  private static Map<Integer,Integer> snakes(int size) {
    return size==64 ? Map.of(47,26, 62,18)
         :            Map.of(87,24, 73,15);
  }

  private static Map<Integer,Integer> ladders(int size) {
    return size==64 ? Map.of(4,14, 22,33)
         :            Map.of(11,29, 40,68);
  }

  private List<OverlayParams> loadOverlays(int size) {
        /* choose file based on size ------------------------------------------------------- */
        String resource;
        switch (size) {
            case 64  -> resource = "/overlays/overlays64.json";
            case 90  -> resource = "/overlays/overlays90.json";
            case 120 -> resource = "/overlays/overlays120.json";
            default  -> { return List.of(); }   // no overlays for custom sizes
        }

        try (InputStream in = getClass().getResourceAsStream(resource)) {
            if (in == null) {
                System.err.println("Overlay file not found: " + resource);
                return List.of();
            }

            /* parse ---------------------------------------------------------------------- */
            JsonNode root = MAPPER.readTree(in);
            List<OverlayParams> list = new ArrayList<>();
            for (JsonNode n : root) {
                list.add(new OverlayParams(
                        n.get("image").asText(),
                        n.get("dx").asDouble(),
                        n.get("dy").asDouble(),
                        n.get("width").asDouble(),
                        n.get("start").asInt()));
            }
            return List.copyOf(list);

        } catch (IOException ex) {
            ex.printStackTrace();
            return List.of();
        }
  }
}