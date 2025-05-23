package edu.ntnu.idatt2003.presentation.service.player;

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

/**
 * UI service implementation for Ludo game player interface components.
 * Handles creating and updating player display elements specific to Ludo,
 * including multiple game pieces per player and turn indicators.
 */
public class LudoPlayerUIService implements PlayerUIService {
  private static final int TOKEN_SIZE = 35;

  /**
   * Creates a player display box for Ludo.
   * Currently delegates to the SNL implementation since the basic player box
   * layout works for both games.
   *
   * @param player  the player to create the display box for
   * @param hasTurn whether this player currently has the turn
   * @return a Node containing the player's display elements
   */
  @Override
  public Node createPlayerBox(PlayerView player, boolean hasTurn) {
    return new SnlPlayerUiService().createPlayerBox(player, hasTurn);
  }

  /**
   * Creates an ImageView for a player's game token/piece in Ludo.
   * Loads the appropriate token image and sets up the basic display properties.
   *
   * @param tokenName the name of the token (e.g., "red", "blue", "green",
   *                  "yellow")
   * @return an ImageView displaying the player's token
   */
  @Override
  public ImageView createTokenImage(String tokenName) {
    String path = ResourcePaths.IMAGE_DIR + tokenName.toLowerCase() + "Piece.png";
    ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
    iv.setFitWidth(40);
    iv.setFitHeight(40);
    iv.getStyleClass().add("player-figure");
    return iv;
  }

  /**
   * Updates the visual indicators for whose turn it is in Ludo.
   * Shows/hides turn indicators and applies golden glow effects to highlight
   * the active player, similar to the SNL implementation.
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
   * Creates all four game pieces for a Ludo player.
   * Each player in Ludo has 4 pieces that can move around the board.
   * Highlights the currently active piece with a golden glow effect.
   *
   * @param player the player to create pieces for
   * @return a list of ImageViews representing the player's 4 game pieces
   */
  @Override
  public List<ImageView> createPlayerPieces(PlayerView player) {
    List<ImageView> pieces = new ArrayList<>();
    String tokenName = player.playerToken();

    for (int i = 0; i < player.piecePositions().size(); i++) {
      String imgFile = tokenName.toLowerCase() + "Piece.png";
      ImageView iv = new ImageView(
          new Image(getClass().getResourceAsStream(ResourcePaths.IMAGE_DIR + imgFile)));
      iv.setFitWidth(TOKEN_SIZE);
      iv.setFitHeight(TOKEN_SIZE);
      iv.setUserData(tokenName + "_" + i);

      if (player.hasTurn() && player.activePieceIndex() == i) {
        DropShadow highlight = new DropShadow(10, Color.GOLD);
        highlight.setSpread(0.8);
        iv.setEffect(highlight);
      }

      pieces.add(iv);
    }

    return pieces;
  }

  /**
   * Updates the player display panel for Ludo.
   * Currently not implemented - placeholder for future functionality.
   *
   * @param container the container to update
   * @param players   the list of players to display
   */
  @Override
  public void updatePlayerDisplay(Node container, List<PlayerView> players) {
    // Implementation for updating player display in control panel
  }

  /**
   * Creates the "current player turn" display box for Ludo.
   * Shows whose turn it is with their token image and a turn message.
   * The display includes proper spacing and styling for the Ludo interface.
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
   * Updates the current player turn display for Ludo.
   * Changes the displayed token, updates the turn message, and applies visual
   * effects.
   * Shows a waiting message when no player is active, or the current player's
   * info
   * with a golden glow effect when someone has the turn.
   *
   * @param turnBox       the turn display container to update
   * @param currentPlayer the player whose turn it is (null for waiting state)
   * @param message       custom message to display (null uses default "roll the
   *                      dice" message)
   */
  @Override
  public void updateCurrentPlayerTurnBox(Node turnBox, PlayerView currentPlayer, String message) {
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
  }
}