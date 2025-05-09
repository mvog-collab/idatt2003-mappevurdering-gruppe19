package edu.ntnu.idatt2003.ui.shared.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoadedPlayersPage {
    private final VBox view = new VBox(20);
    private final VBox playersListBox = new VBox(5);
    private final List<CheckBox> playerBoxes = new ArrayList<>();
    private final Button addSelectedButton = new Button("Add selected");
    private final Button cancelButton = new Button("Back");

    /** Tar imot spiller-objektene som nettopp ble lest fra fil */
    public LoadedPlayersPage(List<String[]> rows) {

        Label title = new Label("Load players");
        title.getStyleClass().add("choose-player-title-label");

        for (String[] row : rows) {
            CheckBox checkBox = new CheckBox(
                    "%s  (%s, %s)".formatted(row[0], row[1], row[2]));
            checkBox.setUserData(row);
            playerBoxes.add(checkBox);
            playersListBox.getChildren().add(checkBox);
        }
        playersListBox.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(20, cancelButton, addSelectedButton);
        buttons.setAlignment(Pos.CENTER);

        view.getChildren().addAll(title, new Separator(), playersListBox, buttons);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.CENTER);
        view.getStyleClass().add("page-background");
        view.getStylesheets()
            .add(getClass().getResource("/styles/style.css").toExternalForm());
    }

    public VBox getView() {
        return view; 
    }

    public Button getaddSelectedButton() {
        return addSelectedButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public List<String[]> getSelectedRows() {
        return playerBoxes.stream()
                          .filter(CheckBox::isSelected)
                          .map(cb -> (String[]) cb.getUserData())
                          .toList();
    }
}