package edu.ntnu.idatt2003.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class Dialogs {

    private Dialogs() {}

    public static void info   (String header, String body) {
        alert(Alert.AlertType.INFORMATION, header, body);
    }

    public static void warn   (String header, String body) {
        alert(Alert.AlertType.WARNING,     header, body);
    }

    public static void error  (String header, String body) {
        alert(Alert.AlertType.ERROR,       header, body);
    }


    private static void alert(Alert.AlertType type, String header, String message) {
        Platform.runLater(() -> {
            Alert a = new Alert(type);
            a.setTitle("Snakes & Ladders");
            a.setHeaderText(header);
            a.setContentText(message);
            a.showAndWait();
        });
    }
}