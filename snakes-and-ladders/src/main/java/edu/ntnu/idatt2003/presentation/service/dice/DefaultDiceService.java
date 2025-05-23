package edu.ntnu.idatt2003.presentation.service.dice;

import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Default dice service implementation for games using two dice.
 * <p>
 * Manages dual die display with rotation effects for standard board games.
 * </p>
 */
public class DefaultDiceService implements DiceService {

  private static final int DIE_SIDE = 50;
  private ImageView die1Img;
  private ImageView die2Img;

  @Override
  public void initializeDice(Pane container) {
    String imgDir = ResourcePaths.IMAGE_DIR;
    die1Img = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
    die2Img = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));

    die1Img.setFitWidth(DIE_SIDE);
    die1Img.setFitHeight(DIE_SIDE);

    die2Img.setFitWidth(DIE_SIDE);
    die2Img.setFitHeight(DIE_SIDE);

    die1Img.setRotate(-5);
    die2Img.setRotate(25);

    container.getChildren().addAll(die1Img, die2Img);
  }

  @Override
  public void showDice(Pane container, int[] values) {
    String imgDir = ResourcePaths.IMAGE_DIR;

    // Update first die
    if (values.length > 0) {
      die1Img.setImage(new Image(getClass().getResourceAsStream(imgDir + values[0] + ".png")));
      die1Img.setRotate(Math.random() * 360);
    }

    // Update second die if available
    if (values.length > 1) {
      die2Img.setImage(new Image(getClass().getResourceAsStream(imgDir + values[1] + ".png")));
      die2Img.setRotate(Math.random() * 360);
    }
  }

  @Override
  public int[] parseDiceRoll(Object diceData) {
    if (diceData instanceof List<?> diceValues) {
      int[] result = new int[diceValues.size()];

      for (int i = 0; i < diceValues.size(); i++) {
        if (diceValues.get(i) instanceof Integer value) {
          result[i] = value;
        }
      }

      return result;
    }

    return new int[0];
  }
}