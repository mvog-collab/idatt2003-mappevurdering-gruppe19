package edu.ntnu.idatt2003.presentation.service;

import edu.ntnu.idatt2003.presentation.service.animation.AnimationService;
import edu.ntnu.idatt2003.presentation.service.animation.BoardAnimationService;
import edu.ntnu.idatt2003.presentation.service.board.BoardUIService;
import edu.ntnu.idatt2003.presentation.service.board.LudoBoardUIService;
import edu.ntnu.idatt2003.presentation.service.board.SnlBoardUIService;
import edu.ntnu.idatt2003.presentation.service.dice.DefaultDiceService;
import edu.ntnu.idatt2003.presentation.service.dice.DiceService;
import edu.ntnu.idatt2003.presentation.service.dice.LudoDiceService;
import edu.ntnu.idatt2003.presentation.service.player.SnlPlayerUiService;
import edu.ntnu.idatt2003.presentation.service.player.LudoPlayerUIService;
import edu.ntnu.idatt2003.presentation.service.player.PlayerUIService;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

/**
 * Factory for creating game-specific view services.
 * <p>
 * Provides centralized creation of UI services based on game type,
 * ensuring appropriate implementations are used for each game variant.
 * </p>
 */
public class ViewServiceFactory {
  /** Logger for this factory */
  private static final Logger LOG = Logger.getLogger(ViewServiceFactory.class.getName());

  /**
   * Private constructor to prevent instantiation.
   */
  private ViewServiceFactory() {
  }

  /**
   * Creates a board UI service for the specified game type.
   *
   * @param gameType  the type of game (SNL, LUDO)
   * @param boardSize the size of the board
   * @return the appropriate board UI service implementation
   * @throws IllegalArgumentException if game type is unknown
   */
  public static BoardUIService createBoardUIService(String gameType, int boardSize) {
    LOG.info(() -> "Creating BoardUIService for gameType: " + gameType + ", boardSize: " + boardSize);
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
        return new SnlBoardUIService();
      case "LUDO":
        return new LudoBoardUIService();
      default:
        LOG.log(Level.SEVERE, "Unknown game type for BoardUIService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for BoardUIService: " + gameType);
    }
  }

  /**
   * Creates an animation service for the specified game type.
   *
   * @param gameType    the type of game
   * @param coordinates map of tile coordinates for positioning
   * @param tokenPane   the pane containing game tokens
   * @return the appropriate animation service implementation
   * @throws IllegalArgumentException if parameters are null or game type is
   *                                  unknown
   */
  public static AnimationService createAnimationService(
      String gameType, Map<Integer, Point2D> coordinates, Pane tokenPane) {
    LOG.info(() -> "Creating AnimationService for gameType: " + gameType);
    if (coordinates == null || tokenPane == null) {
      LOG.log(Level.SEVERE, "Cannot create AnimationService: coordinates or tokenPane is null.");
      throw new IllegalArgumentException("Coordinates and tokenPane cannot be null for AnimationService creation.");
    }
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
      case "LUDO":
        return new BoardAnimationService(coordinates, tokenPane);
      default:
        LOG.log(Level.SEVERE, "Unknown game type for AnimationService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for AnimationService: " + gameType);
    }
  }

  /**
   * Creates a player UI service for the specified game type.
   *
   * @param gameType the type of game
   * @return the appropriate player UI service implementation
   * @throws IllegalArgumentException if game type is unknown
   */
  public static PlayerUIService createPlayerUIService(String gameType) {
    LOG.info(() -> "Creating PlayerUIService for gameType: " + gameType);
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
        return new SnlPlayerUiService();
      case "LUDO":
        return new LudoPlayerUIService();
      default:
        LOG.log(Level.SEVERE, "Unknown game type for PlayerUIService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for PlayerUIService: " + gameType);
    }
  }

  /**
   * Creates a dice service for the specified game type.
   *
   * @param gameType the type of game
   * @return the appropriate dice service implementation
   * @throws IllegalArgumentException if game type is unknown
   */
  public static DiceService createDiceService(String gameType) {
    LOG.info(() -> "Creating DiceService for gameType: " + gameType);
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
        return new DefaultDiceService();
      case "LUDO":
        return new LudoDiceService();
      default:
        LOG.log(Level.SEVERE, "Unknown game type for DiceService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for DiceService: " + gameType);
    }
  }
}