package edu.ntnu.idatt2003.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Errors {
    private static final Logger LOG = Logger.getLogger("ERROR");

    private Errors() {}

    /** log + show short message */
    public static void handle(String userMessage, Exception ex) {
        LOG.log(Level.SEVERE, userMessage, ex);          // full stack-trace for the log file
        Dialogs.error("Oops…", userMessage);             // polite version for the player
    }
}
