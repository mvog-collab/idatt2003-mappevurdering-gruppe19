package edu.ntnu.idatt2003.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Helper class for displaying JavaFX alerts on the GUI thread.
 * <p>
 * Supports information, warning, and error dialogs, and ensures
 * that all {@link Alert}s are shown via {@link Platform#runLater}.
 * </p>
 */
public final class Dialogs {

  private Dialogs() {
    // Prevent instantiation
  }

  /**
   * Shows an information dialog with the given header and message.
   *
   * @param header the title text of the dialog
   * @param body   the content text of the dialog
   */
  public static void info(String header, String body) {
    alert(Alert.AlertType.INFORMATION, header, body);
  }

  /**
   * Shows a warning dialog with the given header and message.
   *
   * @param header the title text of the dialog
   * @param body   the content text of the dialog
   */
  public static void warn(String header, String body) {
    alert(Alert.AlertType.WARNING, header, body);
  }

  /**
   * Shows an error dialog with the given header and message.
   *
   * @param header the title text of the dialog
   * @param body   the content text of the dialog
   */
  public static void error(String header, String body) {
    alert(Alert.AlertType.ERROR, header, body);
  }

  /**
   * Creates and displays an {@link Alert} on the JavaFX application thread.
   *
   * @param type    the type of alert (INFORMATION, WARNING, ERROR)
   * @param header  the header text of the alert
   * @param message the content text of the alert
   */
  private static void alert(Alert.AlertType type, String header, String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(type);
      alert.setTitle("Snakes & Ladders");
      alert.setHeaderText(header);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }
}