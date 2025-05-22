package edu.ntnu.idatt2003.ui.shared.view;

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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

public class ViewServiceFactory {
  private static final Logger LOG = Logger.getLogger(ViewServiceFactory.class.getName());

  public static BoardUIService createBoardUIService(String gameType, int boardSize) {
    LOG.info(() -> "Creating BoardUIService for gameType: " + gameType + ", boardSize: " + boardSize);
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
        return new SnlBoardUIService(boardSize);
      case "LUDO":
        return new LudoBoardUIService();
      default:
        LOG.log(Level.SEVERE, "Unknown game type for BoardUIService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for BoardUIService: " + gameType);
    }
  }

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

  public static PlayerUIService createPlayerUIService(String gameType) {
    LOG.info(() -> "Creating PlayerUIService for gameType: " + gameType);
    String normalizedGameType = gameType.toUpperCase();
    switch (normalizedGameType) {
      case "SNL":
        return new DefaultPlayerUIService();
      case "LUDO":
        return new LudoPlayerUIService();
      default:
        LOG.log(Level.SEVERE, "Unknown game type for PlayerUIService: " + gameType);
        throw new IllegalArgumentException("Unknown game type for PlayerUIService: " + gameType);
    }
  }

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