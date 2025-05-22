package edu.ntnu.idatt2003.ui.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.ArrayList;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LudoPlayerUIService implements PlayerUIService {
  private static final int TOKEN_SIZE = 35;

  @Override
  public Node createPlayerBox(PlayerView player, boolean hasTurn) {
    // Similar to DefaultPlayerUIService but with Ludo-specific styling if needed
    return new DefaultPlayerUIService().createPlayerBox(player, hasTurn);
  }

  @Override
  public ImageView createTokenImage(String tokenName) {
    String path = ResourcePaths.IMAGE_DIR + tokenName.toLowerCase() + "Piece.png";
    ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
    iv.setFitWidth(40);
    iv.setFitHeight(40);
    iv.getStyleClass().add("player-figure");
    return iv;
  }

  @Override
  public void updateTurnIndicator(Node playerBox, boolean hasTurn) {
    // Similar to DefaultPlayerUIService
    if (!(playerBox instanceof VBox box))
      return;

    // Update style class
    if (hasTurn) {
      box.getStyleClass().add("current-player");
    } else {
      box.getStyleClass().remove("current-player");
    }

    // Update turn indicator and token glow
    for (Node node : box.getChildren()) {
      if (node instanceof Label label && label.getStyleClass().contains("turn-indicator")) {
        label.setVisible(hasTurn);
      }

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
    // Create 4 pieces for each Ludo player
    List<ImageView> pieces = new ArrayList<>();
    String tokenName = player.token();

    for (int i = 0; i < player.piecePositions().size(); i++) {
      String imgFile = tokenName.toLowerCase() + "Piece.png";
      ImageView iv = new ImageView(
          new Image(getClass().getResourceAsStream(ResourcePaths.IMAGE_DIR + imgFile)));
      iv.setFitWidth(TOKEN_SIZE);
      iv.setFitHeight(TOKEN_SIZE);
      iv.setUserData(tokenName + "_" + i);

      // Highlight active piece if needed
      if (player.hasTurn() && player.activePieceIndex() == i) {
        DropShadow highlight = new DropShadow(10, Color.GOLD);
        highlight.setSpread(0.8);
        iv.setEffect(highlight);
      }

      pieces.add(iv);
    }

    return pieces;
  }

  @Override
  public void updatePlayerDisplay(Node container, List<PlayerView> players) {
    // Implementation for updating player display in control panel
  }

  // In LudoPlayerUIService.java AND DefaultPlayerUIService.java
  @Override
  public Node createCurrentPlayerTurnBox(PlayerView currentPlayer) {
    VBox container = new VBox();
    container.setSpacing(10);
    container.setPadding(new Insets(15));
    container.setAlignment(Pos.CENTER);
    container.getStyleClass().add("current-player-box");
    container.setUserData("current-player-turn-box");

    ImageView tokenImg = new ImageView();
    tokenImg.setFitWidth(50);
    tokenImg.setFitHeight(50);
    tokenImg.setPreserveRatio(true);

    Label turnMessageLabel = new Label();
    turnMessageLabel.setWrapText(true);
    turnMessageLabel.getStyleClass().add("turn-message");

    HBox contentBox = new HBox(15);
    contentBox.setAlignment(Pos.CENTER_LEFT);
    contentBox.getChildren().addAll(tokenImg, turnMessageLabel);
    HBox.setHgrow(turnMessageLabel, Priority.ALWAYS);

    container.getChildren().add(contentBox);

    container.setMinHeight(Region.USE_PREF_SIZE);

    updateCurrentPlayerTurnBox(container, currentPlayer, null);

    return container;
  }

  @Override
  public void updateCurrentPlayerTurnBox(Node turnBox, PlayerView currentPlayer, String message) {
    if (!(turnBox instanceof VBox container))
      return;

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

    if (tokenImg == null || turnMessageLabel == null)
      return;

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
