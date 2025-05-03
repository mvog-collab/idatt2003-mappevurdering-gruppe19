package edu.ntnu.idatt2003.ui.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage extends Application {


  @Override
  public void start(Stage stage) {
    show(stage);
  }

  public void show(Stage stage) {
    Label title = new Label("Retro Roll & Rise");
    title.getStyleClass().add("home-page-title");

    // Stigespill-knapp
    ImageView laddersBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/SnakeAndLadder.png")));
    laddersBtn.setFitWidth(250);
    laddersBtn.setFitHeight(250);
    laddersBtn.getStyleClass().add("menu-button-image");

    laddersBtn.setOnMouseClicked((MouseEvent e) -> {
      try {

        SnlPage snlPage = new SnlPage();
        stage.setScene(snlPage.getScene());

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    laddersBtn.setOnMouseEntered(e -> {
      laddersBtn.setScaleX(1.1);
      laddersBtn.setScaleY(1.1);
    });
    laddersBtn.setOnMouseExited(e -> {
      laddersBtn.setScaleX(1.0);
      laddersBtn.setScaleY(1.0);
    });

    // Ludo-knapp
    ImageView ludoBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/Ludo.png")));
    ludoBtn.setFitWidth(250);
    ludoBtn.setFitHeight(250);
    ludoBtn.getStyleClass().add("menu-button-image");
    ludoBtn.setOnMouseClicked((MouseEvent e) -> {
      try {
        LudoPage ludoPage = new LudoPage();
        stage.setScene(ludoPage.getScene());
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    });
    ludoBtn.setOnMouseEntered(e -> {
      ludoBtn.setScaleX(1.1);
      ludoBtn.setScaleY(1.1);
    });
    ludoBtn.setOnMouseExited(e -> {
      ludoBtn.setScaleX(1.0);
      ludoBtn.setScaleY(1.0);
    });


    HBox buttonBox = new HBox(40, laddersBtn, ludoBtn);
    buttonBox.setAlignment(Pos.CENTER);

    VBox root = new VBox(40, title, buttonBox);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
    root.getStyleClass().add("page-background");

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

    stage.setTitle("Retro Roll & Rise");
    stage.setScene(scene);
    stage.show();
  }
}
