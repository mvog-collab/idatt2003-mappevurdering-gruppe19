package edu.ntnu.idatt2003.ui;

import edu.ntnu.idatt2003.ui.navigation.NavigationService;
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

  private Label title;
  private ImageView laddersBtn;
  private ImageView ludoBtn;
  private HBox buttonBox;
  private VBox root;

  @Override
  public void start(Stage stage) {
    NavigationService.getInstance().initialize(stage); // Initialize NavigationService
    stage.setScene(createScene(stage)); // Create and set the scene for HomePage
    stage.setTitle("Retro Roll & Rise");
    stage.show();
  }

  public Scene createScene(Stage stageForEventHandlers) {
    buildUI();
    setupEventHandlers(stageForEventHandlers);
    Scene homeScene = new Scene(root, 800, 600);
    homeScene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    return homeScene;
  }

  private void buildUI() {
    title = new Label("Retro Roll & Rise");
    title.getStyleClass().add("home-page-title");
    laddersBtn = createGameButton("SnakeAndLadder.png");
    ludoBtn = createGameButton("Ludo.png");
    buttonBox = new HBox(40, laddersBtn, ludoBtn);
    buttonBox.setAlignment(Pos.CENTER);
    root = new VBox(40, title, buttonBox);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
    root.getStyleClass().add("page-background");
  }

  private ImageView createGameButton(String imageName) {
    ImageView button =
        new ImageView(new Image(getClass().getResourceAsStream("/images/" + imageName)));
    button.setFitWidth(250);
    button.setFitHeight(250);
    button.getStyleClass().add("menu-button-image");
    button.setOnMouseEntered(
        e -> {
          button.setScaleX(1.1);
          button.setScaleY(1.1);
        });
    button.setOnMouseExited(
        e -> {
          button.setScaleX(1.0);
          button.setScaleY(1.0);
        });
    return button;
  }

  private void setupEventHandlers(Stage stage) {
    laddersBtn.setOnMouseClicked(
        (MouseEvent e) -> NavigationService.getInstance().navigateToSnlPage());

    ludoBtn.setOnMouseClicked(
        (MouseEvent e) -> NavigationService.getInstance().navigateToLudoPage());
  }
}
