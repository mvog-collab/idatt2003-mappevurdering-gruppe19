package edu.ntnu.idatt2003.ui.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    // Store token as user data for lookup
    box.setUserData(player.token());

    // Create token image
    ImageView tokenImg = createTokenImage(player.token());
    tokenImg.setFitWidth(50);
    tokenImg.setFitHeight(50);

    // Create turn indicator
    Label turnLabel = new Label("\uD83C\uDFB2 Your turn!");
    turnLabel.getStyleClass().add("turn-indicator");
    turnLabel.setVisible(hasTurn);

    // Create name label
    Label nameLabel = new Label(player.name());
    nameLabel.getStyleClass().add("player-name");

    // Assemble box
    box.getChildren().addAll(turnLabel, tokenImg, nameLabel);

    // Add styling for current player
    if (hasTurn) {
      box.getStyleClass().add("current-player");

      // Add glow effect to token
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

      // Update token glow effect
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

  @Override
  public Node createCurrentPlayerTurnBox(PlayerView currentPlayer) {
    // Create the main container
    VBox container = new VBox();
    container.setSpacing(10);
    container.setPadding(new Insets(15));
    container.setAlignment(Pos.CENTER);
    container.getStyleClass().add("current-player-box");
    container.setUserData("current-player-turn-box"); // For identification

    // Create the token image view
    ImageView tokenImg = new ImageView();
    tokenImg.setFitWidth(50);
    tokenImg.setFitHeight(50);
    tokenImg.setPreserveRatio(true);

    // Create the turn message label
    Label turnMessageLabel = new Label();
    turnMessageLabel.setWrapText(true);
    turnMessageLabel.getStyleClass().add("turn-message");

    // Create content box to hold token and message side by side
    HBox contentBox = new HBox(15);
    contentBox.setAlignment(Pos.CENTER_LEFT);
    contentBox.getChildren().addAll(tokenImg, turnMessageLabel);
    HBox.setHgrow(turnMessageLabel, Priority.ALWAYS);

    // Add the content to the container
    container.getChildren().add(contentBox);

    // Set default properties
    container.setMinHeight(100);
    container.setPrefWidth(400);

    // Update with player data if present
    updateCurrentPlayerTurnBox(container, currentPlayer, null);

    return container;
  }

  @Override
  public void updateCurrentPlayerTurnBox(Node turnBox, PlayerView currentPlayer, String message) {
    if (!(turnBox instanceof VBox container)) return;

    // Find the components
    HBox contentBox = null;
    ImageView tokenImg = null;
    Label turnMessageLabel = null;

    if (!container.getChildren().isEmpty() && container.getChildren().get(0) instanceof HBox) {
      contentBox = (HBox) container.getChildren().get(0);

      if (!contentBox.getChildren().isEmpty()
          && contentBox.getChildren().get(0) instanceof ImageView) {
        tokenImg = (ImageView) contentBox.getChildren().get(0);
      }

      if (contentBox.getChildren().size() > 1 && contentBox.getChildren().get(1) instanceof Label) {
        turnMessageLabel = (Label) contentBox.getChildren().get(1);
      }
    }

    if (tokenImg == null || turnMessageLabel == null) return;

    if (currentPlayer == null) {
      // No player has a turn - show default state
      turnMessageLabel.setText("Waiting for game to start...");
      tokenImg.setImage(null);
      tokenImg.setEffect(null);
      container.getStyleClass().remove("active");
    } else {
      // Player has a turn - update display
      String playerName = currentPlayer.name();
      String tokenName = currentPlayer.token();

      // Update the message
      if (message != null && !message.isEmpty()) {
        turnMessageLabel.setText(message);
      } else {
        turnMessageLabel.setText(playerName + "'s turn! Roll the dice 🎲");
      }

      // Update the token image
      String imagePath = "/images/" + tokenName.toLowerCase() + "Piece.png";
      tokenImg.setImage(new Image(getClass().getResourceAsStream(imagePath)));

      // Add a glow effect to the token
      DropShadow glow = new DropShadow(15, Color.GOLD);
      glow.setSpread(0.4);
      tokenImg.setEffect(glow);

      // Add active styling
      if (!container.getStyleClass().contains("active")) {
        container.getStyleClass().add("active");
      }
    }
  }
}
