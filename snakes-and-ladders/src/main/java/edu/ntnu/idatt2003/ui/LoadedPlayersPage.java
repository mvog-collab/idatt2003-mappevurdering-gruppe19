package edu.ntnu.idatt2003.ui;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idatt2003.models.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class LoadedPlayersPage {
    private final VBox view = new VBox(20);
    private final VBox playersListBox = new VBox(5);
    private final List<CheckBox> playerCheckBoxes = new ArrayList<>();
    private final Button loadPlayersButton = new Button("Add selected");
    private final Button cancelButton      = new Button("Back");

    /** Tar imot spiller-objektene som nettopp ble lest fra fil */
    public LoadedPlayersPage(List<Player> players) {
        Label title = new Label("Load players");
        title.getStyleClass().add("choose-player-title-label");

        // Lag én CheckBox per spiller
        for (Player p : players) {
            CheckBox cb = new CheckBox(p.getName());
            cb.setUserData(p);                 // lagre Player-objektet i boksen
            playerCheckBoxes.add(cb);
            playersListBox.getChildren().add(cb);
        }
        playersListBox.setAlignment(Pos.CENTER);

        VBox buttons = new VBox(10, cancelButton, loadPlayersButton);
        buttons.setAlignment(Pos.CENTER);

        view.getChildren().addAll(title, new Separator(), playersListBox, buttons);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.CENTER);
        view.getStyleClass().add("page-background");
        view.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    }

    public VBox   getView()               { return view; }
    public Button getLoadPlayersButton()  { return loadPlayersButton; }
    public Button getCancelButton()       { return cancelButton; }

    /** Hvilke spillere har brukeren huket av? */
    public List<Player> getSelectedPlayers() {
        return playerCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (Player) cb.getUserData())
                .toList();
    }
}