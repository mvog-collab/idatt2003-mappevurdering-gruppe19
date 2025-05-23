package edu.ntnu.idatt2003.utils;

import java.util.logging.Logger;

/**
 * Centralized loggers for different application categories.
 * <p>
 * Provides named {@link Logger} instances for game events and rule processing.
 * </p>
 */
public final class Log {

  private Log() {
    // Prevent instantiation
  }

  /**
   * Returns the logger used for game-related messages.
   *
   * @return the "GAME" logger
   */
  public static Logger game() {
    return Logger.getLogger("GAME");
  }

  /**
   * Returns the logger used for rule-engine messages.
   *
   * @return the "RULES" logger
   */
  public static Logger rules() {
    return Logger.getLogger("RULES");
  }
}