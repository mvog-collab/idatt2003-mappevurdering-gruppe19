package edu.ntnu.idatt2003.presentation.shared.view;

import edu.ntnu.idatt2003.utils.ResourcePaths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JavaFX view for displaying and selecting from loaded player data.
 * <p>
 * Presents a list of players loaded from a CSV file with checkboxes
 * for selection. Users can choose which players to add to their game.
 * </p>
 */
public class LoadedPlayersPage {

  private static final Logger LOG = Logger.getLogger(LoadedPlayersPage.class.getName());
  private final VBox view = new VBox(20);
  private final VBox playersListBox = new VBox(5);
  private final List<CheckBox> playerBoxes = new ArrayList<>();
  private final Button addSelectedButton = new Button("Add selected");
  private final Button cancelButton = new Button("Back");

  /**
   * Constructs a new LoadedPlayersPage with the specified player data.
   *
   * @param rows list of string arrays containing player data (name, token,
   *             birthday)
   */
  public LoadedPlayersPage(List<String[]> rows) {
    Label title = new Label("Load players");
    title.getStyleClass().add("choose-player-title-label");

    for (String[] row : rows) {
      CheckBox checkBox = new CheckBox("%s  (%s, %s)".formatted(row[0], row[1], row[2]));
      checkBox.setUserData(row);
      checkBox.getStyleClass().add("loaded-player");
      playerBoxes.add(checkBox);
      playersListBox.getChildren().add(checkBox);
    }
    playersListBox.setAlignment(Pos.CENTER);

    HBox buttons = new HBox(20, cancelButton, addSelectedButton);
    buttons.setAlignment(Pos.CENTER);
    addSelectedButton.getStyleClass().add("confirm-button");
    cancelButton.getStyleClass().add("exit-button");

    view.getChildren().addAll(title, new Separator(), playersListBox, buttons);
    view.setPadding(new Insets(30));
    view.setAlignment(Pos.CENTER);
    view.getStyleClass().add("page-background");

    java.net.URL cssUrl = getClass().getResource(ResourcePaths.STYLE_SHEET);
    if (cssUrl != null) {
      view.getStylesheets().add(cssUrl.toExternalForm());
    } else {
      LOG.warning("Stylesheet not found: " + ResourcePaths.STYLE_SHEET);
    }
  }

  /**
   * Gets the root view container.
   *
   * @return the root VBox container
   */
  public VBox getView() {
    return view;
  }

  /**
   * Gets the button for adding selected players.
   *
   * @return the add selected button
   */
  public Button getAddSelectedButton() {
    return addSelectedButton;
  }

  /**
   * Gets the cancel button.
   *
   * @return the cancel button
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Gets the data for all currently selected players.
   *
   * @return list of string arrays containing selected player data
   */
  public List<String[]> getSelectedRows() {
    return playerBoxes.stream()
        .filter(CheckBox::isSelected)
        .map(cb -> (String[]) cb.getUserData())
        .toList();
  }
}