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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
    Button btn = new Button("?");
    btn.getStyleClass().add("icon-button");
    btn.setOnAction(
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
    return btn;
  }

  private Button createGoToHomeButton() {
    Button homeButton = new Button("Home");
    homeButton.getStyleClass().add("navigation-button");
    homeButton.setOnAction(e -> NavigationService.getInstance().navigateToHome());
    return homeButton;
  }

  private Button createGoBackToGameSetupButton() {
    Button backButton = new Button("Back");
    backButton.getStyleClass().add("navigation-button");
    backButton.setOnAction(e -> NavigationService.getInstance().goBackToGameSetupPage());
    return backButton;
  }

  protected HBox createTopBarWithNavigationAndHelp(
      boolean includeBackButtonToGameSetup, Button helpButton) {
    HBox navBox = new HBox(20);
    navBox.setPadding(new Insets(10));

    Button homeButton = createGoToHomeButton();
    navBox.getChildren().add(homeButton);

    if (includeBackButtonToGameSetup) {
      Button backButton = createGoBackToGameSetupButton();
      navBox.getChildren().add(backButton);
    }

    if (helpButton != null) {
      HBox rightAlignBox = new HBox(helpButton);
      navBox.getChildren().addAll(rightAlignBox);
    }

    return navBox;
  }

  protected void addNavigationAndHelpToBorderPane(
      BorderPane root, boolean includeBackButtonToGameSetup, Button helpButton) {
    HBox topBar = createTopBarWithNavigationAndHelp(includeBackButtonToGameSetup, helpButton);
    BorderPane.setAlignment(topBar, Pos.TOP_LEFT);
    root.getChildren().add(topBar);
  }
}
