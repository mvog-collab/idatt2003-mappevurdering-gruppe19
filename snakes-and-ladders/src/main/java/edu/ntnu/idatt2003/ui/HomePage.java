package edu.ntnu.idatt2003.ui;

import edu.ntnu.idatt2003.ui.navigation.NavigationService;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
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
    private Scene homeScene;

    @Override
    public void start(Stage stage) {
        NavigationService.getInstance().initialize(stage);
        stage.setScene(createScene(stage));
        stage.setTitle("Retro Roll & Rise");
        stage.show();
    }

    public Scene createScene(Stage stageForEventHandlers) {
        buildUI();
        setupEventHandlers(stageForEventHandlers);
        homeScene = new Scene(root, 800, 600);
        homeScene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        double imageButtonWidthFactor = 0.22;
        double imageButtonHeightFactor = 0.30;
        double minImageWidth = 220;
        double minImageHeight = 220;

        laddersBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getWidth() * imageButtonWidthFactor,
                                minImageWidth),
                        homeScene.widthProperty()));
        laddersBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getHeight() * imageButtonHeightFactor,
                                minImageHeight),
                        homeScene.heightProperty()));

        ludoBtn.fitWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getWidth() * imageButtonWidthFactor,
                                minImageWidth),
                        homeScene.widthProperty()));
        ludoBtn.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> Math.max(
                                homeScene.getHeight() * imageButtonHeightFactor,
                                minImageHeight),
                        homeScene.heightProperty()));

        buttonBox.spacingProperty().bind(homeScene.widthProperty().multiply(0.05));
        root.spacingProperty().bind(homeScene.heightProperty().multiply(0.05));

        root.paddingProperty()
                .bind(
                        Bindings.createObjectBinding(
                                () -> new Insets(
                                        homeScene.heightProperty().doubleValue() * 0.05,
                                        homeScene.widthProperty().doubleValue() * 0.05,
                                        homeScene.heightProperty().doubleValue() * 0.05,
                                        homeScene.widthProperty().doubleValue() * 0.05),
                                homeScene.widthProperty(),
                                homeScene.heightProperty()));

        return homeScene;
    }

    private void buildUI() {
        title = new Label("Retro Roll & Rise");
        title.getStyleClass().add("home-page-title");
        title.setWrapText(true);
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
        ImageView button = new ImageView(new Image(getClass().getResourceAsStream("/images/" + imageName)));
        button.setFitWidth(250);
        button.setFitHeight(250);
        button.setPickOnBounds(true);
        button.setPreserveRatio(true);
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
