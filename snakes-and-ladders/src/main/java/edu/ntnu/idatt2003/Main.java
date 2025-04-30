package edu.ntnu.idatt2003;

import edu.ntnu.idatt2003.ui.view.SnlPage;

public class Main  {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                           "[%1$tF %1$tT] [%4$s] %5$s %n");
        java.util.logging.LogManager.getLogManager()
                                    .getLogger("")
                                    .setLevel(java.util.logging.Level.INFO);
    }

    public static void main(String[] args) {
        SnlPage.launch(args);
    }
}