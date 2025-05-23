package edu.ntnu.idatt2003.presentation.service.player;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * UI service implementation for Snakes and Ladders player interface components.
 * Handles creating and updating player display elements like token images,
 * turn indicators, and player info boxes.
 */
public class SnlPlayerUiService implements PlayerUIService {

  /**
   * Creates a complete player display box showing the player's token, name, and
   * turn status.
   * The box includes a dice emoji when it's the player's turn and adds a golden
   * glow effect
   * to make the active player stand out.
   *
   * @param player  the player to create the display box for
   * @param hasTurn whether this player currently has the turn
   * @return a VBox containing all the player's display elements
   */
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

    box.setUserData(player.playerToken());

    ImageView tokenImg = createTokenImage(player.playerToken());
    tokenImg.setFitWidth(50);
    tokenImg.setFitHeight(50);

    Label turnLabel = new Label("\uD83C\uDFB2 Your turn!");
    turnLabel.getStyleClass().add("turn-indicator");
    turnLabel.setVisible(hasTurn);

    Label nameLabel = new Label(player.playerName());
    nameLabel.getStyleClass().add("player-playerName");

    box.getChildren().addAll(turnLabel, tokenImg, nameLabel);

    if (hasTurn) {
      box.getStyleClass().add("current-player");
    }

    return box;
  }

  /**
   * Creates an ImageView for a player's game token/piece.
   * Loads the image from resources based on the token name and applies basic
   * styling.
   *
   * @param tokenName the name of the token (e.g., "red", "blue")
   * @return an ImageView displaying the player's token
   */
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

  /**
   * Updates the visual indicators for whose turn it is.
   * Shows/hides the turn indicator label and adds/removes the golden glow effect
   * on the player's token to highlight the active player.
   *
   * @param playerBox the player's display box to update
   * @param hasTurn   whether this player now has the turn
   */
  @Override
  public void updateTurnIndicator(Node playerBox, boolean hasTurn) {
    if (!(playerBox instanceof VBox box))
      return;

    if (hasTurn) {
      box.getStyleClass().add("current-player");
    } else {
      box.getStyleClass().remove("current-player");
    }

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

  /**
   * Creates multiple game pieces for a player.
   * Not implemented for Snakes and Ladders since players only have one piece.
   *
   * @param player the player to create pieces for
   * @return null (not applicable for SNL)
   */
  @Override
  public List<ImageView> createPlayerPieces(PlayerView player) {
    return null;
  }

  /**
   * Updates the player display panel.
   * Currently not implemented for SNL.
   *
   * @param container the container to update
   * @param players   the list of players to display
   */
  @Override
  public void updatePlayerDisplay(Node container, List<PlayerView> players) {
    // Implementation for updating player panel
  }

  /**
   * Creates the "current player turn" display box that shows whose turn it is.
   * This creates a prominent display with the player's token and a message
   * indicating it's their turn to play.
   *
   * @param currentPlayer the player whose turn it is
   * @return a styled container showing the current player info
   */
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

  /**
   * Updates the current player turn display with new player info and message.
   * Changes the token image, updates the turn message, and applies visual effects
   * to highlight the active player. If no player is provided, shows a waiting
   * state.
   *
   * @param turnBox       the turn display container to update
   * @param currentPlayer the player whose turn it is (null for waiting state)
   * @param message       custom message to display (null for default message)
   */
  @Override
  public void updateCurrentPlayerTurnBox(Node turnBox, PlayerView currentPlayer, String message) {
    try {
      if (!(turnBox instanceof VBox container))
        return;

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
        turnMessageLabel.setText("Waiting for game to start...");
        tokenImg.setImage(null);
        tokenImg.setEffect(null);
        container.getStyleClass().remove("active");
      } else {
        String playerName = currentPlayer.playerName();
        String tokenName = currentPlayer.playerToken();

        if (message != null && !message.isEmpty()) {
          turnMessageLabel.setText(message);
        } else {
          turnMessageLabel.setText(playerName + "'s turn! Roll the dice 🎲");
        }

        String imagePath = "/images/" + tokenName.toLowerCase() + "Piece.png";
        tokenImg.setImage(new Image(getClass().getResourceAsStream(imagePath)));

        DropShadow glow = new DropShadow(15, Color.GOLD);
        glow.setSpread(0.4);
        tokenImg.setEffect(glow);

        if (!container.getStyleClass().contains("active")) {
          container.getStyleClass().add("active");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}