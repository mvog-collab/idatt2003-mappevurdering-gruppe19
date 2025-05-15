package edu.ntnu.idatt2003.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class UiDialogs {
  private UiDialogs() {}

  public static Stage createModalPopup(String title, Parent root, int width, int height) {
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle(title);
    Scene scene = new Scene(root, width, height);
    scene
        .getStylesheets()
        .add(UiDialogs.class.getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    scene.getRoot().requestFocus();
    stage.setScene(scene);
    return stage;
  }
}
