package edu.ntnu.idatt2003.ui.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LudoPlayerUIService implements PlayerUIService {
  private static final int TOKEN_SIZE = 30;

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
    if (!(playerBox instanceof VBox box)) return;

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
      ImageView iv =
          new ImageView(
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
}
