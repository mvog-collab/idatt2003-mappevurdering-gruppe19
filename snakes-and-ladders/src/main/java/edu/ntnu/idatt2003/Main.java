package edu.ntnu.idatt2003;

import edu.ntnu.idatt2003.presentation.HomePage;
import edu.ntnu.idatt2003.utils.Errors;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Entry point for the boardgame application.
 * <p>
 * Responsible for configuring the logging system, setting up a global
 * exception handler, and launching the JavaFX UI.
 * </p>
 */
public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  /**
   * Static initializer block to load custom logging properties.
   * <p>
   * Attempts to read "logging.properties" from the classpath. If not found
   * or an error occurs, falls back to the default logging configuration
   * and emits a warning.
   * </p>
   */
  static {
    try (InputStream is = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
      if (is == null) {
        LOG.log(Level.WARNING,
            "Warning: logging.properties not found. Using default logging configuration.");
      } else {
        LogManager.getLogManager().readConfiguration(is);
      }
    } catch (Exception e) {
      LOG.log(Level.WARNING, "Error loading logging configuration: ", e);
      e.printStackTrace();
    }
  }

  /**
   * Starts the JavaFX application for the Ludo game.
   * <p>
   * Sets up a default uncaught exception handler to catch any runtime
   * exceptions thrown on any thread, logs them at SEVERE level, and
   * displays a user-friendly error via {@link Errors#handle}.
   * </p>
   *
   * @param args command-line arguments passed to the JavaFX launcher
   */
  public static void main(String[] args) {
    LOG.info("Application starting...");

    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      LOG.log(Level.SEVERE, "Uncaught exception in thread " + t.getName(), e);
      Errors.handle("An unexpected problem occurred. Please restart the game.", (Exception) e);
    });

    try {
      javafx.application.Application.launch(HomePage.class, args);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to launch application", e);
      Errors.handle("Application failed to launch. Please check logs for details.", e);
    }

    LOG.info(
        "Application main method finished. If UI is not showing, check previous logs for errors.");
  }
}