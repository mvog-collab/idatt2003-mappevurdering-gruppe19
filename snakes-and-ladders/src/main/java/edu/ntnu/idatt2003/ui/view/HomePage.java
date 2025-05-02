package edu.ntnu.idatt2003.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

  public class HomePage {

    public void show(Stage stage) {
      Label title = new Label("Retro Roll & Rise");
      title.getStyleClass().add("title");

      // Game buttons (as images)
      ImageView laddersBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/SnakeAndLadder.png")));
      laddersBtn.setFitWidth(150);
      laddersBtn.setFitHeight(150);
      laddersBtn.setOnMouseClicked((MouseEvent e) -> {
        System.out.println("Start Stigespill");
        // TODO: Koble til stigespill
      });

      laddersBtn.getStyleClass().add("menu-button-image");


      ImageView ludoBtn = new ImageView(new Image(getClass().getResourceAsStream("/images/Ludo.png")));
      ludoBtn.setFitWidth(150);
      ludoBtn.setFitHeight(150);
      ludoBtn.setOnMouseClicked((MouseEvent e) -> {
        System.out.println("Start Ludo");
        // TODO: Koble til ludo
      });

      ludoBtn.getStyleClass().add("menu-button-image");

      HBox buttonsBox = new HBox(40, laddersBtn, ludoBtn);
      buttonsBox.setAlignment(Pos.CENTER);


      VBox root = new VBox(40, title, buttonsBox);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(40));

      Scene scene = new Scene(root, 800, 600);
      stage.setTitle("Retro Roll & Rise");
      stage.setScene(scene);
      stage.show();
    }
  }
