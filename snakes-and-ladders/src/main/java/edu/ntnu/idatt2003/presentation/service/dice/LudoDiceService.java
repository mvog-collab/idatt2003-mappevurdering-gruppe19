package edu.ntnu.idatt2003.presentation.service.dice;

import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class LudoDiceService implements DiceService {
  private static final int DIE_SIDE = 50;
  private ImageView dieImg;

  @Override
  public void initializeDice(Pane container) {
    // Create single die image for Ludo
    String imgDir = ResourcePaths.IMAGE_DIR;
    dieImg = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
    dieImg.setFitWidth(DIE_SIDE);
    dieImg.setFitHeight(DIE_SIDE);

    // Add to container
    container.getChildren().add(dieImg);
  }

  @Override
  public void showDice(Pane container, int[] values) {
    String imgDir = ResourcePaths.IMAGE_DIR;

    // Update die if we have a value
    if (values.length > 0) {
      dieImg.setImage(new Image(getClass().getResourceAsStream(imgDir + values[0] + ".png")));
      dieImg.setRotate(Math.random() * 360);
    }
  }

  @Override
  public int[] parseDiceRoll(Object diceData) {
    if (diceData instanceof List<?> diceValues && !diceValues.isEmpty()) {
      if (diceValues.get(0) instanceof Integer value) {
        return new int[] {value};
      }
    }
    return new int[0];
  }
}
