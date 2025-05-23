package edu.ntnu.idatt2003.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * Centralized error handling for both logging and user notification.
 * <p>
 * Logs full exception details at SEVERE level and shows a user-friendly
 * error dialog on the JavaFX application thread.
 * </p>
 */
public final class Errors {

  private static final Logger LOG = Logger.getLogger(Errors.class.getName());

  private Errors() {
    // Prevent instantiation
  }

  /**
   * Handles an exception by logging it and showing an error dialog.
   *
   * @param userMessage the message to display to the user
   * @param ex          the exception to log
   */
  public static void handle(String userMessage, Exception ex) {
    LOG.log(Level.SEVERE,
        userMessage + " (See exception details below)",
        ex);

    if (Platform.isFxApplicationThread()) {
      Dialogs.error("Oops… Something Went Wrong", userMessage);
    } else {
      Platform.runLater(
          () -> Dialogs.error("Oops… Something Went Wrong", userMessage));
    }
  }

  /**
   * Logs a severe-level message and exception.
   *
   * @param message the message to log
   * @param ex      the throwable to log
   */
  public static void logSevere(String message, Throwable ex) {
    LOG.log(Level.SEVERE, message, ex);
  }

  /**
   * Logs a warning-level message and exception.
   *
   * @param message the message to log
   * @param ex      the throwable to log
   */
  public static void logWarning(String message, Throwable ex) {
    LOG.log(Level.WARNING, message, ex);
  }

  /**
   * Logs a warning-level message.
   *
   * @param message the message to log
   */
  public static void logWarning(String message) {
    LOG.log(Level.WARNING, message);
  }
}