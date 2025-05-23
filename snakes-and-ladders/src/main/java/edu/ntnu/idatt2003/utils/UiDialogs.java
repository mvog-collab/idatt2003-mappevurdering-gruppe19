package edu.ntnu.idatt2003.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Helper class for creating and configuring modal JavaFX windows.
 * <p>
 * Builds a new {@link Stage} in {@link Modality#APPLICATION_MODAL} mode,
 * applies the standard stylesheet, and returns it ready for display.
 * </p>
 */
public final class UiDialogs {

  private UiDialogs() {
    // Prevent instantiation
  }

  /**
   * Creates a modal popup window with the specified title, content, and size.
   *
   * @param title  the window title
   * @param root   the root node to display in the popup
   * @param width  the desired width in pixels
   * @param height the desired height in pixels
   * @return a configured {@link Stage} ready to be shown
   */
  public static Stage createModalPopup(String title, Parent root, int width, int height) {
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle(title);

    Scene scene = new Scene(root, width, height);
    scene.getStylesheets()
        .add(UiDialogs.class.getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    scene.getRoot().requestFocus();

    stage.setScene(scene);
    return stage;
  }
}