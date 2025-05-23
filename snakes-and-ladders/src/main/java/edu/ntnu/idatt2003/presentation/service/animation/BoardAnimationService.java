package edu.ntnu.idatt2003.presentation.service.animation;

import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Animation service implementation for board-based games.
 * <p>
 * Provides smooth token movement animations using coordinate mapping
 * and threaded animation with JavaFX Platform updates.
 * </p>
 */
public class BoardAnimationService implements AnimationService {

  private final Map<Integer, Point2D> tileCoordinates;
  private final Pane tokenPane;

  /**
   * Constructs a new BoardAnimationService.
   *
   * @param tileCoordinates map of tile positions
   * @param tokenPane       pane containing tokens
   */
  public BoardAnimationService(Map<Integer, Point2D> tileCoordinates, Pane tokenPane) {
    this.tileCoordinates = tileCoordinates;
    this.tokenPane = tokenPane;
  }

  @Override
  public void animateMove(String tokenName, int startId, int endId, Runnable onFinished) {
    ImageView token = findToken(tokenName);
    if (token == null) {
      if (onFinished != null)
        onFinished.run();
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
            Platform.runLater(() -> {
              try {
                moveTokenToTile(token, id); // Or other UI update
              } catch (Exception e) {
                System.err.println("Exception in Platform.runLater in BoardAnimationService:");
                e.printStackTrace();
              }
            });
          }
          if (onFinished != null) {
            Platform.runLater(() -> {
              try {
                onFinished.run();
              } catch (Exception e) {
                System.err.println("Exception in onFinished callback for animation:");
                e.printStackTrace();
              }
            });
          }
        })
        .start();
  }

  @Override
  public void animateMoveAlongPath(
      String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished) {
    ImageView token = findTokenWithIndex(tokenName, pieceIndex);
    if (token == null || path.isEmpty()) {
      if (onFinished != null)
        Platform.runLater(onFinished);
      return;
    }

    new Thread(
        () -> {
          try {
            for (Integer id : path) {
              Thread.sleep(200);
              Platform.runLater(() -> {
                try {
                  moveTokenToTile(token, id); // Or other UI update
                } catch (Exception e) {
                  System.err.println("Exception in Platform.runLater in BoardAnimationService:");
                  e.printStackTrace();
                }
              });
            }
          } catch (InterruptedException ignored) {
          }
          if (onFinished != null) {
            Platform.runLater(() -> {
              try {
                onFinished.run();
              } catch (Exception e) {
                System.err.println("Exception in onFinished callback for animation:");
                e.printStackTrace();
              }
            });
          }
        })
        .start();
  }

  /**
   * Moves a token to the specified tile position.
   *
   * @param token  the token to move
   * @param tileId the target tile identifier
   */
  private void moveTokenToTile(ImageView token, int tileId) {
    Point2D target = tileCoordinates.get(tileId);
    if (target != null) {
      token.setLayoutX(target.getX() - token.getFitWidth() / 2);
      token.setLayoutY(target.getY() - token.getFitHeight() / 2);
    }
  }

  /**
   * Finds a token by name in the token pane.
   *
   * @param tokenName the name of the token to find
   * @return the token ImageView, or null if not found
   */
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

  /**
   * Finds a specific piece token by name and index.
   *
   * @param tokenName  the name of the token
   * @param pieceIndex the index of the piece
   * @return the token ImageView, or null if not found
   */
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