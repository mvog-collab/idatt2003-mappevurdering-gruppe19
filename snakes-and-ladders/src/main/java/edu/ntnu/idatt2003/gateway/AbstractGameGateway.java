package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.games.engine.impl.DefaultGame;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.model.Token;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.store.PlayerStore;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;

public abstract class AbstractGameGateway implements CompleteBoardGame {
    // Common fields
    private final List<BoardGameObserver> observers = new ArrayList<>();
    protected final PlayerStore playerStore;
    protected final OverlayProvider overlayProvider;
    protected final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();
    protected List<Integer> lastDiceValues = List.of();
    protected DefaultGame game;
    
    // Constructor
    protected AbstractGameGateway(PlayerStore playerStore, OverlayProvider overlayProvider) {
        this.playerStore = playerStore;
        this.overlayProvider = overlayProvider;
    }
    
    // Common implementation of observer methods
    @Override
    public void addObserver(BoardGameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(BoardGameObserver observer) {
        observers.remove(observer);
    }
    
    protected void notifyObservers(BoardGameEvent event) {
        for (BoardGameObserver observer : observers) {
            observer.update(event);
        }
    }
    
    // Common implementation of other GameGateway methods
    @Override
    public boolean hasWinner() {
        return game != null && game.winner().isPresent();
    }
    
    @Override
    public String currentPlayerName() {
        return game != null ? game.currentPlayer().getName() : "";
    }
    
    @Override
    public List<Integer> lastDiceValues() {
        return lastDiceValues;
    }
    
    @Override
    public void savePlayers(Path out) throws IOException {
        if (game != null) {
            playerStore.save(game.players(), out);
        }
    }
    
    @Override
    public List<OverlayParams> boardOverlays() {
        int size = boardSize();
        return overlayCache.computeIfAbsent(size, this::loadOverlays);
    }
    
    // Helper methods
    protected List<OverlayParams> loadOverlays(int size) {
        return overlayProvider.overlaysForBoard(size);
    }
    
    // Methods that require game-specific implementation
    protected abstract Token mapStringToToken(String token);
    
    // Common implementation for loadPlayers
    @Override
    public void loadPlayers(List<String[]> rows) {
        if (rows != null) {
            rows.forEach(r -> {
                if (r.length >= 3) {
                    addPlayer(r[0], r[1], LocalDate.parse(r[2]));
                }
            });
        }
    }
}