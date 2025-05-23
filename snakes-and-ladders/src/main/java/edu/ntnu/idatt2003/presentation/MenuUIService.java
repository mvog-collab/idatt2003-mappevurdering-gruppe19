package edu.ntnu.idatt2003.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MenuUIService {
  // Creates a styled title label for a menu page.
  public Label createTitleLabel(String text, String styleClass) {
    Label label = new Label(text);
    label.getStyleClass().add(styleClass);
    return label;
  }

  // Creates a board preview image with specified dimensions.
  public ImageView createBoardPreview(String imagePath, double width, double height) {
    ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
    imageView.setFitWidth(width);
    imageView.setFitHeight(height);
    imageView.setPreserveRatio(true);
    return imageView;
  }

  // Creates a button with styling for menu pages.
  public Button createMenuButton(String text, String styleClass) {
    Button button = new Button(text);
    button.getStyleClass().add(styleClass);
    return button;
  }

  // Creates a vertical menu panel with buttons.
  public VBox createMenuPanel(Button... buttons) {
    VBox menu = new VBox(30);
    menu.getChildren().addAll(buttons);
    menu.setAlignment(Pos.CENTER_RIGHT);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setMaxWidth(350);
    return menu;
  }

  // Creates a container for the title and board preview.
  public VBox createLeftPanel(Label title, ImageView boardPreview) {
    VBox leftPanel = new VBox(10, title, boardPreview);
    leftPanel.setAlignment(Pos.CENTER);
    return leftPanel;
  }

  // Creates the main layout combining the left panel and menu.
  public HBox createMainLayout(VBox leftPanel, VBox menuPanel) {
    HBox layout = new HBox(10, leftPanel, menuPanel);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));
    layout.getStyleClass().add("page-background");
    return layout;
  }
}
