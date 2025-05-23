package edu.ntnu.idatt2003;

import edu.ntnu.idatt2003.presentation.HomePage;
import edu.ntnu.idatt2003.utils.Errors;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  static {
    try (InputStream is = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
      if (is == null) {
        LOG.log(Level.WARNING, "Warning: logging.properties not found. Using default logging configuration.");
      } else {
        LogManager.getLogManager().readConfiguration(is);
      }
    } catch (Exception e) {
      LOG.log(Level.WARNING, "Error loading logging configuration: ", e);
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    LOG.info("Application starting...");
    Thread.setDefaultUncaughtExceptionHandler(
        (t, e) -> {
          LOG.log(Level.SEVERE, "Uncaught exception in thread " + t.getName(), e);
          Errors.handle("An unexpected problem occurred. Please restart the game.", (Exception) e);
        });

    try {
      javafx.application.Application.launch(HomePage.class, args);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to launch application", e);
      Errors.handle("Application failed to launch. Please check logs for details.", e);
    }
    LOG.info("Application main method finished. If UI is not showing, check previous logs for errors.");
  }
}