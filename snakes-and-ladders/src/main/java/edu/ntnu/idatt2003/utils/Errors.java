package edu.ntnu.idatt2003.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public final class Errors {
  private static final Logger LOG = Logger.getLogger(Errors.class.getName());

  private Errors() {
  }

  public static void handle(String userMessage, Exception ex) {
    // Log the full stack trace for developers
    LOG.log(Level.SEVERE, userMessage + " (See exception details below)", ex);

    // Show a polite version of the message to the player on the JavaFX thread
    if (Platform.isFxApplicationThread()) {
      Dialogs.error("Oops… Something Went Wrong", userMessage);
    } else {
      Platform.runLater(() -> Dialogs.error("Oops… Something Went Wrong", userMessage));
    }
  }

  public static void logSevere(String message, Throwable ex) {
    LOG.log(Level.SEVERE, message, ex);
  }

  public static void logWarning(String message, Throwable ex) {
    LOG.log(Level.WARNING, message, ex);
  }

  public static void logWarning(String message) {
    LOG.log(Level.WARNING, message);
  }
}