package edu.ntnu.idatt2003.ui.service.animation;

import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class BoardAnimationService implements AnimationService {
  private final Map<Integer, Point2D> tileCoordinates;
  private final Pane tokenPane;

  public BoardAnimationService(Map<Integer, Point2D> tileCoordinates, Pane tokenPane) {
    this.tileCoordinates = tileCoordinates;
    this.tokenPane = tokenPane;
  }

  @Override
  public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
    ImageView token = findToken(tokenName);
    if (token == null) {
      if (onFinished != null) onFinished.run();
      return;
    }

    new Thread(
            () -> {
              for (int i = startId + 1; i <= endId; i++) {
                int id = i;
                try {
                  Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(() -> moveTokenToTile(token, id));
              }
              if (onFinished != null) Platform.runLater(onFinished);
            })
        .start();
  }

  @Override
  public void animateMoveAlongPath(
      String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished) {
    ImageView token = findTokenWithIndex(tokenName, pieceIndex);
    if (token == null || path.isEmpty()) {
      if (onFinished != null) Platform.runLater(onFinished);
      return;
    }

    new Thread(
            () -> {
              try {
                for (Integer id : path) {
                  Thread.sleep(200);
                  Platform.runLater(() -> moveTokenToTile(token, id));
                }
              } catch (InterruptedException ignored) {
              }
              if (onFinished != null) Platform.runLater(onFinished);
            })
        .start();
  }

  private void moveTokenToTile(ImageView token, int tileId) {
    Point2D target = tileCoordinates.get(tileId);
    if (target != null) {
      token.setLayoutX(target.getX() - token.getFitWidth() / 2);
      token.setLayoutY(target.getY() - token.getFitHeight() / 2);
    }
  }

  private ImageView findToken(String tokenName) {
    // Log for debugging
    System.out.println(
        "Finding playerToken: " + tokenName + " among " + tokenPane.getChildren().size() + " children");

    // Check ID-based lookup first
    for (javafx.scene.Node node : tokenPane.getChildren()) {
      if (node instanceof ImageView imageView) {
        // Try multiple identification methods
        if (tokenName.equals(node.getUserData())) {
          return imageView;
        }

        if (node.getId() != null && node.getId().equals(tokenName + "Token")) {
          return imageView;
        }

        if (imageView.getImage() != null) {
          // Just log that we found an image without userData
          System.out.println("Found image without userData for playerToken: " + tokenName);
        }
      }
    }
    for (javafx.scene.Node node : tokenPane.getChildren()) {
      if (node instanceof ImageView) {
        System.out.println("No match found, using first playerToken as fallback");
        return (ImageView) node;
      }
    }

    return null;
  }

  private ImageView findTokenWithIndex(String tokenName, int pieceIndex) {
    // For games with multiple pieces per player (like Ludo)
    int count = 0;
    for (javafx.scene.Node node : tokenPane.getChildren()) {
      if (node instanceof ImageView imageView) {
        if (node.getUserData() != null
            && node.getUserData().toString().startsWith(tokenName + "_")
            && count++ == pieceIndex) {
          return imageView;
        }

        if (node.getId() != null && node.getId().equals(tokenName + "Token_" + pieceIndex)) {
          return imageView;
        }
      }
    }
    return null;
  }
}
