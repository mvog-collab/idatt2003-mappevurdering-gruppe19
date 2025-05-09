package edu.games.engine.strategy.factory;

import edu.games.engine.strategy.GameStrategy;
import edu.games.engine.strategy.LudoGameStrategy;
import edu.games.engine.strategy.SnlGameStrategy;
import edu.ntnu.idatt2003.persistence.BoardAdapter;

public class GameStrategyFactory {
    
    private GameStrategyFactory() {
        // Private constructor to prevent instantiation
    }
    
    public static GameStrategy createLudoStrategy() {
        return new LudoGameStrategy();
    }
    
    public static GameStrategy createSnlStrategy(BoardAdapter.MapData mapData) {
        return new SnlGameStrategy(mapData.snakes(), mapData.ladders());
    }
}