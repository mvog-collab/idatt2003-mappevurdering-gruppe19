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
import edu.games.engine.model.PlayerPiece;
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


    private final DiceFactory diceFactory;
    private final PlayerStore playerStore;
    private final OverlayProvider overlayProvider;

    private final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();
    private final RuleConfig ruleConfig = new RuleConfig(RuleConfig.ExtraTurnPolicy.ON_SIX);
    private List<Integer> lastDice = List.of(1);
    private int selectedPieceIndex = -1;
    private Player winner = null;

    private DefaultGame game;

    private static final Map<String,Token> TOKEN_MAP = Map.of(
        "BLUE",   Token.BLUE,
        "GREEN",  Token.GREEN,
        "YELLOW", Token.YELLOW,
        "RED",    Token.RED);

    public LudoGateway(DiceFactory diceFactory,
                       PlayerStore playerStore,
                       OverlayProvider overlayProvider) {
        this.diceFactory = diceFactory;
        this.playerStore = playerStore;
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
        Dice dice = diceFactory.create();
        game = new DefaultGame(board, rules, new ArrayList<>(), dice);
        winner = null;
    }

    @Override
    public void newGame(BoardAdapter.MapData data) {
      newGame(0);
    }

    @Override
    public void resetGame() {
        if (game == null) return;
        game.players().forEach(p -> {
            for (PlayerPiece piece : p.getPieces()) {
                piece.moveTo(null);
            }
        });
        game.setCurrentPlayerIndex(0);
        winner = null;
    }

    @Override
    public void addPlayer(String name, String token, LocalDate birthday) {
        Objects.requireNonNull(game, "Call newGame before adding players");
        Player player = new Player(name, TOKEN_MAP.get(token), birthday);
        game.players().add(player);
    }

    @Override
    public void loadPlayers(List<String[]> rows) {
        rows.forEach(r -> addPlayer(r[0], r[1], LocalDate.parse(r[2])));
    }

    @Override
    public void savePlayers(Path out) throws IOException {
        playerStore.save(game.players(), out);
    }

    // New method to select a piece for the current player
    public void selectPiece(int pieceIndex) {
        if (game.players().isEmpty()) return;
        
        // Validate the piece index
        if (pieceIndex < 0 || pieceIndex >= 4) {
            throw new IllegalArgumentException("Invalid piece index: " + pieceIndex);
        }
        
        // Store the selected piece
        selectedPieceIndex = pieceIndex;
    }

    @Override
    public int rollDice() {
        if (game.players().isEmpty() || winner != null) return 0;
        
        // Roll the dice first
        int rollValue = game.dice().roll();
        lastDice = game.dice().lastValues();
        
        // Get current player
        Player currentPlayer = game.currentPlayer();
        Log.game().info(() -> "%s rolled %s".formatted(currentPlayer.getName(), lastDice));
        
        // If no piece is selected yet, return the roll value without moving
        if (selectedPieceIndex < 0) {
            return rollValue;
        }
        
        // Get the selected piece
        PlayerPiece selectedPiece = currentPlayer.getPiece(selectedPieceIndex);
        LudoColor color = LudoColor.valueOf(currentPlayer.getToken().name());
        LudoBoard board = (LudoBoard) game.board();
        
        // If piece is at home and roll is 6, move it to the start position
        if (selectedPiece.isAtHome() && rollValue == 6) {
            Tile startTile = board.getStartTile(color);
            selectedPiece.moveTo(startTile);
            
            // Player gets another turn when rolling a 6
            return rollValue;
        }
        
        // If piece is on board, move it according to the rules
        if (selectedPiece.isOnBoard()) {
            // Get the current tile and calculate next position
            Tile currentTile = selectedPiece.getCurrentTile();
            
            // Use the path's next method to calculate the destination tile
            Tile nextTile = board.move(currentTile, rollValue, color);
            
            // Move the piece
            if (nextTile != null) {
                selectedPiece.moveTo(nextTile);
                
                // Check for bumping other pieces
                bumpOtherPieces(selectedPiece, nextTile);
                
                // Check for winner - if all pieces are in goal
                checkForWinner(currentPlayer);
                
                // Move to next player if not rolling a 6
                if (rollValue != 6) {
                    int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
                    game.setCurrentPlayerIndex(nextIndex);
                }
            }
        } else {
            // No valid move - move to next player
            int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
            game.setCurrentPlayerIndex(nextIndex);
        }
        
        // Reset the selected piece
        selectedPieceIndex = -1;
        
        return rollValue;
    }

    // Helper method to bump other players' pieces back to home
    private void bumpOtherPieces(PlayerPiece movedPiece, Tile destination) {
        if (destination == null) return;
        
        // Skip bumping in goal tiles (ID > 52)
        if (destination.id() > 52) return;
        
        Player currentPlayer = game.currentPlayer();
        
        game.players().stream()
            .filter(p -> p != currentPlayer) // Only check other players
            .forEach(player -> {
                player.getPieces().stream()
                    .filter(piece -> piece.isOnBoard() &&
                           piece.getCurrentTile().id() == destination.id())
                    .forEach(piece -> {
                        // Send the piece back home
                        piece.moveTo(null);
                        Log.game().info(() -> 
                            "%s bumps %s's piece back to home"
                            .formatted(currentPlayer.getName(), player.getName()));
                    });
            });
    }

    // Helper method to check if a player has won (all pieces in goal area)
    private void checkForWinner(Player player) {
        boolean allInGoal = player.getPieces().stream()
            .filter(piece -> piece.isOnBoard())
            .allMatch(piece -> piece.getCurrentTile().id() > 52 && 
                     piece.getCurrentTile().id() < 77);
        
        if (allInGoal && player.getPieces().stream().allMatch(piece -> piece.isOnBoard())) {
            winner = player;
        }
    }

/**
 * Applies movement for the selected piece without rolling the dice again.
 * This uses the last rolled value instead.
 * 
 * @return The last rolled value
 */
/**
 * Applies movement for the selected piece without rolling the dice again.
 * This uses the last rolled value instead.
 * 
 * @return The last rolled value
 */
public int applyPieceMovement() {
    if (game.players().isEmpty() || winner != null || selectedPieceIndex < 0) return 0;
    
    // Get the last roll value
    int rollValue = lastDice.get(0);
    
    // Get current player
    Player currentPlayer = game.currentPlayer();
    
    // Get the selected piece
    PlayerPiece selectedPiece = currentPlayer.getPiece(selectedPieceIndex);
    LudoColor color = LudoColor.valueOf(currentPlayer.getToken().name());
    LudoBoard board = (LudoBoard) game.board();
    
    // Track if the piece was initially at home
    boolean wasAtHome = selectedPiece.isAtHome();
    
    // If piece is at home and roll is 6, move it to the start position
    if (wasAtHome && rollValue == 6) {
        Tile startTile = board.getStartTile(color);
        selectedPiece.moveTo(startTile);
        
        selectedPieceIndex = -1;
        return rollValue;
    }
    
    // If piece is on board, move it according to the rules
    if (selectedPiece.isOnBoard()) {
        // Get the current tile and calculate next position
        Tile currentTile = selectedPiece.getCurrentTile();
        
        // Use the path's next method to calculate the destination tile
        Tile nextTile = board.move(currentTile, rollValue, color);
        
        // Move the piece
        if (nextTile != null) {
            selectedPiece.moveTo(nextTile);
            
            // Check for bumping other pieces
            bumpOtherPieces(selectedPiece, nextTile);
            
            // Check for winner - if all pieces are in goal
            checkForWinner(currentPlayer);
            
            // Move to next player if not rolling a 6 OR if we just moved from home
            // This is the key change - we don't give a third turn after moving from home
            if (rollValue != 6 || wasAtHome) {
                int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
                game.setCurrentPlayerIndex(nextIndex);
            }
        }
    } else {
        // No valid move - move to next player
        int nextIndex = (game.players().indexOf(currentPlayer) + 1) % game.players().size();
        game.setCurrentPlayerIndex(nextIndex);
    }
    
    // Reset the selected piece
    selectedPieceIndex = -1;
    
    return rollValue;
}

    @Override
    public boolean hasWinner() {
        return winner != null;
    }

    @Override
    public String currentPlayerName() {
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

    @Override
    public List<PlayerView> players() {
        if (game.players().isEmpty()) return List.of();
        Token turnToken = game.currentPlayer().getToken();
        
        return game.players().stream()
            .map(p -> {

                // Get all piece positions
                List<Integer> piecePositions = p.getPieces().stream()
                    .map(piece -> {
                        if (piece.getCurrentTile() == null) {
                            return 0; // 0 means the piece is at home
                        } else {
                            return piece.getCurrentTile().id();
                        }
                    })
                    .toList();
                
                // Active piece is the selected piece for the current player
                int activePieceIndex = (p.getToken() == turnToken) ? selectedPieceIndex : -1;
                
                return new PlayerView(p.getName(), p.getToken().name(), 
                    piecePositions, p.getBirtday(), 
                    p.getToken() == turnToken, activePieceIndex);
            })
            .toList();
    }

    @Override
    public List<Integer> lastDiceValues() {
        return lastDice;
    }
}