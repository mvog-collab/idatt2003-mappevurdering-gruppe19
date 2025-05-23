package edu.ntnu.idatt2003.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Service class providing reusable UI component factories for menu layouts.
 * <p>
 * Encapsulates creation of styled labels, image previews, buttons, and panels.
 * </p>
 */
public class MenuUIService {

  /**
   * Creates a title label with the specified text and CSS class.
   *
   * @param text       the label text
   * @param styleClass the CSS style class to apply
   * @return a configured {@link Label}
   */
  public Label createTitleLabel(String text, String styleClass) {
    Label label = new Label(text);
    label.getStyleClass().add(styleClass);
    return label;
  }

  /**
   * Creates an image preview, loading from a classpath resource.
   *
   * @param imagePath the classpath path to the image
   * @param width     desired width in pixels
   * @param height    desired height in pixels
   * @return a configured {@link ImageView}
   */
  public ImageView createBoardPreview(String imagePath, double width, double height) {
    ImageView imageView = new ImageView(
        new Image(getClass().getResource(imagePath).toExternalForm()));
    imageView.setFitWidth(width);
    imageView.setFitHeight(height);
    imageView.setPreserveRatio(true);
    return imageView;
  }

  /**
   * Creates a styled button for menu actions.
   *
   * @param text       the button label text
   * @param styleClass the CSS style class to apply
   * @return a configured {@link Button}
   */
  public Button createMenuButton(String text, String styleClass) {
    Button button = new Button(text);
    button.getStyleClass().add(styleClass);
    return button;
  }

  /**
   * Creates a vertical panel containing the given buttons.
   *
   * @param buttons the buttons to include in the panel
   * @return a {@link VBox} with spacing and right alignment
   */
  public VBox createMenuPanel(Button... buttons) {
    VBox menu = new VBox(30);
    menu.getChildren().addAll(buttons);
    menu.setAlignment(Pos.CENTER_RIGHT);
    menu.getStyleClass().add("menu-start-buttons");
    menu.setMaxWidth(350);
    return menu;
  }

  /**
   * Creates a left-side panel combining a title and board preview.
   *
   * @param title        the title {@link Label}
   * @param boardPreview the board preview {@link ImageView}
   * @return a {@link VBox} with spacing and center alignment
   */
  public VBox createLeftPanel(Label title, ImageView boardPreview) {
    VBox leftPanel = new VBox(10, title, boardPreview);
    leftPanel.setAlignment(Pos.CENTER);
    return leftPanel;
  }

  /**
   * Combines left and menu panels into a main layout.
   *
   * @param leftPanel the left-side {@link VBox}
   * @param menuPanel the right-side {@link VBox}
   * @return an {@link HBox} with padding, spacing, and background styling
   */
  public HBox createMainLayout(VBox leftPanel, VBox menuPanel) {
    HBox layout = new HBox(10, leftPanel, menuPanel);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));
    layout.getStyleClass().add("page-background");
    return layout;
  }
}