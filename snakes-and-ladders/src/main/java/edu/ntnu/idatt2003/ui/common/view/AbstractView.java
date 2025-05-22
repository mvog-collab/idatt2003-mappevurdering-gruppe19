package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.navigation.NavigationService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public abstract class AbstractView implements BoardGameObserver {
  protected CompleteBoardGame gateway;

  public void connectToModel(CompleteBoardGame gateway) {
    this.gateway = gateway;
    if (gateway != null) {
      gateway.addObserver(this);
    }
  }

  @Override
  public void update(BoardGameEvent event) {
    Platform.runLater(() -> handleEvent(event));
  }

  protected abstract void handleEvent(BoardGameEvent event);

  protected Button createHowToPlayButton(String title, String instructions) {
    ImageView icon = new ImageView(new Image(getClass().getResource("/images/question-sign.png").toString()));
    icon.setFitWidth(24);
    icon.setFitHeight(24);
    icon.setPreserveRatio(true);

    Button howToPlayButton = new Button();
    howToPlayButton.setGraphic(icon);
    howToPlayButton.getStyleClass().add("icon-button");
    howToPlayButton.setOnAction(
        e -> {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(instructions);

          alert.getDialogPane().getStyleClass().add("how-to-alert");
          alert.getDialogPane().getStylesheets()
              .add(getClass().getResource("/styles/style.css").toExternalForm());
          alert.showAndWait();
        });
    return howToPlayButton;
  }

  private Button createGoToHomeButton() {
    ImageView icon = new ImageView(new Image(getClass().getResource("/images/home.png").toString()));
    icon.setFitWidth(24);
    icon.setFitHeight(24);
    icon.setPreserveRatio(true);

    Button homeButton = new Button();
    homeButton.setGraphic(icon);
    homeButton.getStyleClass().add("icon-button");
    homeButton.setOnAction(e -> NavigationService.getInstance().navigateToHome());
    return homeButton;
  }

  private Button createGoBackToGameSetupButton() {
    ImageView icon = new ImageView(new Image(getClass().getResource("/images/back.png").toString()));
    icon.setFitWidth(24);
    icon.setFitHeight(24);
    icon.setPreserveRatio(true);

    Button backButton = new Button();
    backButton.setGraphic(icon);
    backButton.getStyleClass().add("icon-button");
    backButton.setOnAction(e -> NavigationService.getInstance().goBackToGameSetupPage());
    return backButton;
  }

  protected HBox createTopBarWithNavigationAndHelp(
      boolean includeBackButtonToGameSetup, Button helpButtonInstance) {
    HBox topBar = new HBox();
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    HBox leftAlignedButtons = new HBox(10);
    leftAlignedButtons.setAlignment(Pos.CENTER_LEFT);
    Button homeButton = createGoToHomeButton();
    leftAlignedButtons.getChildren().add(homeButton);

    if (includeBackButtonToGameSetup) {
      Button backButton = createGoBackToGameSetupButton();
      leftAlignedButtons.getChildren().add(backButton);
    }

    topBar.getChildren().add(leftAlignedButtons);

    if (helpButtonInstance != null) {
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);

      HBox rightAlignedButton = new HBox(helpButtonInstance);
      rightAlignedButton.setAlignment(Pos.CENTER_RIGHT);

      topBar.getChildren().addAll(spacer, rightAlignedButton);
    }
    topBar.getStyleClass().add("page-background");

    return topBar;
  }

  protected void addNavigationAndHelpToBorderPane(
      BorderPane root, boolean includeBackButtonToGameSetup, Button helpButton) {
    HBox topBar = createTopBarWithNavigationAndHelp(includeBackButtonToGameSetup, helpButton);
    root.setTop(topBar);
  }
}
