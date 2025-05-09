package edu.ntnu.idatt2003.ui.shared.view;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.util.Map;

import edu.ntnu.idatt2003.ui.service.animation.AnimationService;
import edu.ntnu.idatt2003.ui.service.animation.BoardAnimationService;
import edu.ntnu.idatt2003.ui.service.board.BoardUIService;
import edu.ntnu.idatt2003.ui.service.board.LudoBoardUIService;
import edu.ntnu.idatt2003.ui.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.ui.service.dice.DefaultDiceService;
import edu.ntnu.idatt2003.ui.service.dice.DiceService;
import edu.ntnu.idatt2003.ui.service.dice.LudoDiceService;
import edu.ntnu.idatt2003.ui.service.player.DefaultPlayerUIService;
import edu.ntnu.idatt2003.ui.service.player.LudoPlayerUIService;
import edu.ntnu.idatt2003.ui.service.player.PlayerUIService;

public class ViewServiceFactory {
    public static BoardUIService createBoardUIService(String gameType, int boardSize) {
        return switch (gameType.toUpperCase()) {
            case "SNL" -> new SnlBoardUIService(boardSize);
            case "LUDO" -> new LudoBoardUIService();
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
    
    public static AnimationService createAnimationService(String gameType, 
                                                         Map<Integer, Point2D> coordinates, 
                                                         Pane tokenPane) {
        return switch (gameType.toUpperCase()) {
            case "SNL", "LUDO" -> new BoardAnimationService(coordinates, tokenPane);
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
    
    public static PlayerUIService createPlayerUIService(String gameType) {
        return switch (gameType.toUpperCase()) {
            case "SNL" -> new DefaultPlayerUIService();
            case "LUDO" -> new LudoPlayerUIService();
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
    
    public static DiceService createDiceService(String gameType) {
        return switch (gameType.toUpperCase()) {
            case "SNL" -> new DefaultDiceService();
            case "LUDO" -> new LudoDiceService();
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
}