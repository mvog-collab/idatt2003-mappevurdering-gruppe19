package edu.ntnu.idatt2003.utils;

import java.util.logging.Logger;

public final class Log {
  public static Logger game() {
    return Logger.getLogger("GAME");
  }

  public static Logger rules() {
    return Logger.getLogger("RULES");
  }

  private Log() {}
}
