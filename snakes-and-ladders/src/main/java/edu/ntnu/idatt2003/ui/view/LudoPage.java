package edu.ntnu.idatt2003.ui.view;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.ui.controller.LudoPageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class LudoPage {

    private Scene scene;
    private Button choosePlayerBtn;
    private Button startBtn;
    private Button resetBtn;

    public LudoPage() {
        buildUI();
    }

    private void buildUI() {

    Label title = new Label("Ludo");
    title.getStyleClass().add("ludo-page-title");

    choosePlayerBtn = new Button("Choose players");
    choosePlayerBtn.getStyleClass().add("choose-button");
    startBtn        = new Button("Start game");
    startBtn.getStyleClass().add("confirm-button");
    resetBtn        = new Button("Reset game");
    resetBtn.getStyleClass().add("exit-button");



    GameGateway gw = LudoGateway.createDefault();
    new LudoPageController(this, gw);

    ImageView boardImg = new ImageView(
            new Image(getClass().getResource("/images/ludoBoard.jpg").toExternalForm()));
    boardImg.setFitWidth(350); boardImg.setPreserveRatio(true);

    VBox menu = new VBox(30, choosePlayerBtn, startBtn, resetBtn);
    menu.setAlignment(Pos.CENTER_RIGHT);
    menu.getStyleClass().add("menu-start-buttons");

    VBox leftside = new VBox(title, boardImg);
    leftside.setAlignment(Pos.CENTER_LEFT);
    leftside.setPadding(new Insets(10, 10, 10, 10));
    leftside.setSpacing(20);

    HBox root = new HBox(leftside, menu);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
    root.getStyleClass().add("page-background");



    scene = new Scene(new StackPane(root), 1000, 700);
    scene.getStylesheets().add(
            getClass().getResource("/styles/style.css").toExternalForm());
}

    public Scene getScene()                     { return scene; }
    public Button choosePlayerButton()          { return choosePlayerBtn; }
    public Button startButton()                 { return startBtn; }
    public Button resetButton()                 { return resetBtn; }
}