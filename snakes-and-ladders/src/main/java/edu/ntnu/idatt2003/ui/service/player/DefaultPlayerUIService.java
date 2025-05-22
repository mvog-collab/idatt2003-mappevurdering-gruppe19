package edu.ntnu.idatt2003.ui.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DefaultPlayerUIService implements PlayerUIService {

  @Override
  public Node createPlayerBox(PlayerView player, boolean hasTurn) {
    VBox box = new VBox();
    box.setAlignment(Pos.CENTER);
    box.setSpacing(5);
    box.getStyleClass().add("display-player-box");
    box.setPrefWidth(120);
    box.setMinWidth(100);
    box.setMaxWidth(150);
    box.setPrefHeight(50);

    // Store playerToken as user data for lookup
    box.setUserData(player.playerToken());

    // Create playerToken image
    ImageView tokenImg = createTokenImage(player.playerToken());
    tokenImg.setFitWidth(50);
    tokenImg.setFitHeight(50);

    // Create turn indicator
    Label turnLabel = new Label("\uD83C\uDFB2 Your turn!");
    turnLabel.getStyleClass().add("turn-indicator");
    turnLabel.setVisible(hasTurn);

    // Create playerName label
    Label nameLabel = new Label(player.playerName());
    nameLabel.getStyleClass().add("player-playerName");

    // Assemble box
    box.getChildren().addAll(turnLabel, tokenImg, nameLabel);

    // Add styling for current player
    if (hasTurn) {
      box.getStyleClass().add("current-player");

      // Add glow effect to playerToken
      DropShadow glow = new DropShadow(20, Color.GOLD);
      glow.setSpread(0.5);
      tokenImg.setEffect(glow);
    }

    return box;
  }

  @Override
  public ImageView createTokenImage(String tokenName) {
    String path = ResourcePaths.IMAGE_DIR + tokenName.toLowerCase() + "Piece.png";
    ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
    iv.setFitWidth(40);
    iv.setFitHeight(40);
    iv.getStyleClass().add("player-figure");
    iv.setUserData(tokenName);
    return iv;
  }

  @Override
  public void updateTurnIndicator(Node playerBox, boolean hasTurn) {
    if (!(playerBox instanceof VBox box)) return;

    // Update style class
    if (hasTurn) {
      box.getStyleClass().add("current-player");
    } else {
      box.getStyleClass().remove("current-player");
    }

    // Update turn indicator label
    for (Node node : box.getChildren()) {
      if (node instanceof Label label && label.getStyleClass().contains("turn-indicator")) {
        label.setVisible(hasTurn);
      }

      // Update playerToken glow effect
      if (node instanceof ImageView token) {
        if (hasTurn) {
          DropShadow glow = new DropShadow(20, Color.GOLD);
          glow.setSpread(0.5);
          token.setEffect(glow);
        } else {
          token.setEffect(null);
        }
      }
    }
  }

  @Override
  public List<ImageView> createPlayerPieces(PlayerView player) {
    // Implementation for Ludo with multiple pieces per player
    return null;
  }

  @Override
  public void updatePlayerDisplay(Node container, List<PlayerView> players) {
    // Implementation for updating player panel
  }
}
