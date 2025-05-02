package edu.ntnu.idatt2003.ui.view;

import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.ResourcePaths;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LudoBoardView extends Application {

  private static final int DIE_SIDE = 50;
  private final HBox root = new HBox();

  private final Button rollButton = new Button("Roll dice");
  private final Button againButton = new Button("Play again");

  private ImageView dieImg;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    buildBoardStatic();
    Scene scene = new Scene(root, 1000, 700);
    scene.getStylesheets().add(getClass().getResource(ResourcePaths.STYLE_SHEET).toExternalForm());
    stage.setTitle("LudoBoardView");
    stage.setScene(scene);
    stage.show();
  }

  public Button getRollButton() {
    return rollButton;
  }

  public Button getAgainButton() {
    return againButton;
  }

  public void disableRollButton() {
    rollButton.setDisable(true);
  }

  public void enableRollButton() {
    rollButton.setDisable(false);
  }

  public void announceWinner(String name) {
    disableRollButton();
    Dialogs.info("Winner!", "Congratulations, " + name + "! You won the game");
  }

  private void buildBoardStatic() {
    Pane boardPlaceholder = new Pane();
    boardPlaceholder.setPrefSize(600, 600);
    boardPlaceholder.setStyle("-fx-background-color: grey;");

    // DiceBox
    HBox diceBox = new HBox(10);
    diceBox.setAlignment(Pos.CENTER);
    diceBox.setPrefSize(300, 300);
    diceBox.getStyleClass().add("dice-box");

    String imgDir = ResourcePaths.IMAGE_DIR;
    dieImg = new ImageView(new Image(getClass().getResourceAsStream(imgDir + "1.png")));
    dieImg.setFitWidth(DIE_SIDE);
    dieImg.setFitHeight(DIE_SIDE);
    diceBox.getChildren().add(dieImg);

    // Buttons
    rollButton.getStyleClass().add("roll-dice-button");
    againButton.getStyleClass().add("play-again-button");

    HBox buttons = new HBox(rollButton, againButton);
    buttons.setSpacing(10);
    buttons.setAlignment(Pos.CENTER);

    VBox gameControl = new VBox(diceBox, buttons);
    gameControl.setSpacing(20);
    gameControl.setAlignment(Pos.TOP_CENTER);
    gameControl.setPrefWidth(400);
    gameControl.getStyleClass().add("game-control");

    BorderPane main = new BorderPane();
    main.setCenter(boardPlaceholder);
    main.setRight(gameControl);
    main.setPadding(new Insets(20));
    BorderPane.setAlignment(boardPlaceholder, Pos.CENTER);
    BorderPane.setMargin(boardPlaceholder, new Insets(0, 20, 0, 0));

    main.getStyleClass().add("main-box");

    root.getChildren().setAll(main);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("page-background");
  }

  public void showDice(int d1) {
    String dir = ResourcePaths.IMAGE_DIR;
    dieImg.setImage(new Image(getClass().getResourceAsStream(dir + d1 + ".png")));
    dieImg.setRotate(Math.random() * 360);
  }
}