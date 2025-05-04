package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import edu.games.engine.board.LudoPath;
import edu.games.engine.board.Tile;
import edu.games.engine.board.LudoBoard;
import edu.games.engine.dice.Dice;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.impl.CsvPlayerStore;
import edu.games.engine.impl.overlay.JsonOverlayProvider;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.model.LudoColor;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.rule.LudoRuleEngine;
import edu.games.engine.rule.RuleConfig;
import edu.games.engine.rule.RuleEngine;
import edu.games.engine.store.PlayerStore;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.persistence.BoardAdapter;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import edu.ntnu.idatt2003.utils.Log;

public final class LudoGateway implements GameGateway {


    private final DiceFactory   diceFactory;
    private final PlayerStore   playerStore;
    private final OverlayProvider overlayProvider;

    private final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();
    private final RuleConfig ruleConfig = new RuleConfig(RuleConfig.ExtraTurnPolicy.ON_SIX);
    private       List<Integer> lastDice = List.of(1);

    private DefaultGame game;

    private static final Map<String,Token> TOKEN_MAP = Map.of(
        "BLUE",   Token.BLUE,
        "GREEN",  Token.GREEN,
        "YELLOW", Token.YELLOW,
        "RED",    Token.RED);

    public LudoGateway(DiceFactory diceFactory,
                       PlayerStore playerStore,
                       OverlayProvider overlayProvider) {
        this.diceFactory   = diceFactory;
        this.playerStore   = playerStore;
        this.overlayProvider = overlayProvider;
    }

    public static LudoGateway createDefault() {
        return new LudoGateway(
                new edu.games.engine.dice.factory.RandomDiceFactory(1),
                new CsvPlayerStore(),
                new JsonOverlayProvider("/overlays/"));
    }

    @Override
    public void newGame(int ignored) {
        LudoPath path = new LudoPath();
        LudoBoard board = new LudoBoard(path);
        RuleEngine rules = new LudoRuleEngine(path);
        Dice       dice  = diceFactory.create();
        game = new DefaultGame(board, rules, new ArrayList<>(), dice);
    }

    @Override
    public void newGame(BoardAdapter.MapData data) {
      newGame(0);
    }

    @Override
    public void resetGame() {
        if (game == null) return;
        game.players().forEach(p -> p.moveTo(null));
        game.setCurrentPlayerIndex(0);
    }

    @Override
    public void addPlayer(String name, String token, LocalDate birthday) {
        Objects.requireNonNull(game, "Call newGame before adding players");
        Player player = new Player(name, TOKEN_MAP.get(token), birthday);
        game.players().add(player);
    }

    @Override
    public void loadPlayers(List<String[]> rows) {
        rows.forEach(r -> addPlayer(r[0],r[1],LocalDate.parse(r[2])));
    }

    @Override
    public void savePlayers(Path out) throws IOException {
        playerStore.save(game.players(), out);
    }

    @Override
    public int rollDice() {
        int sum = game.playTurn();
        lastDice = game.dice().lastValues();
        Log.game().info(() -> "%s rolled %s".formatted(currentPlayerName(), lastDice));
        return sum;
    }

    @Override
    public boolean hasWinner()         {
      return game.winner().isPresent();
    }

    @Override
    public String  currentPlayerName() {
      return game.currentPlayer().getName();
    }

    @Override
    public int boardSize() {
      return 57;
    }

    @Override
    public List<OverlayParams> boardOverlays() {
        return overlayCache.computeIfAbsent(boardSize(), overlayProvider::overlaysForBoard);
    }

    //Chat GBT
    @Override
    public List<PlayerView> players() {
        if (game.players().isEmpty()) return List.of();
        Token turnToken = game.currentPlayer().getToken();
        
        return game.players().stream()
            .map(p -> {
                LudoBoard board = (LudoBoard) game.board();
                LudoColor color = LudoColor.valueOf(p.getToken().name());

                int tileId;
                if (p.getCurrentTile() == null) {
                    Tile start = board.getStartTile(color);
                    tileId = (start == null) ? 0 : start.id();  // 0 = "in house"
                } else {
                    tileId = p.getCurrentTile().id();
                    System.out.println("Player " + p.getName() + " (" + p.getToken().name() + 
                                    ") is on tile with ID: " + tileId);
                }

                return new PlayerView(p.getName(), p.getToken().name(), tileId, p.getBirtday(), 
                                    p.getToken() == turnToken);
            })
            .toList();
    }

    @Override
    public List<Integer> lastDiceValues() {
      return lastDice;
    }
}