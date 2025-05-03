package edu.ntnu.idatt2003.ui.view;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.ui.controller.LudoPageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class LudoPage {

    private final Scene scene;

    private final Button choosePlayerBtn = new Button("Choose players");
    private final Button startBtn        = new Button("Start game");
    private final Button resetBtn        = new Button("Reset game");

    public LudoPage() {

        GameGateway gw = LudoGateway.createDefault();
        new LudoPageController(this, gw);

        ImageView boardImg = new ImageView(
                new Image(getClass().getResource("/images/ludoBoard.jpg").toExternalForm()));
        boardImg.setFitWidth(350); boardImg.setPreserveRatio(true);

        VBox menu = new VBox(30, choosePlayerBtn, startBtn, resetBtn);
        menu.setAlignment(Pos.CENTER_RIGHT);
        menu.getStyleClass().add("menu-start-buttons");

        HBox root = new HBox(boardImg, menu);
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