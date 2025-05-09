package edu.ntnu.idatt2003;

import edu.ntnu.idatt2003.ui.shared.view.HomePage;
import edu.ntnu.idatt2003.utils.Errors;

public class Main  {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                           "[%1$tF %1$tT] [%4$s] %5$s %n");
        java.util.logging.LogManager.getLogManager()
                                    .getLogger("")
                                    .setLevel(java.util.logging.Level.INFO);
    }

    public static void main(String[] args) {
        HomePage.launch(args);
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
        Errors.handle("Unexpected problem – please restart the game.", (Exception) e));
    }
}