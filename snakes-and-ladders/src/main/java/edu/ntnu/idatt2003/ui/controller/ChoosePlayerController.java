package edu.ntnu.idatt2003.ui.controller;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.view.LoadedPlayersPage;
import edu.ntnu.idatt2003.utils.csv.PlayerCsv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChoosePlayerController implements BasePopupController {

    private ChoosePlayerPage view;
    private GameGateway gameGateway;
    private String firstPlayerToken;

    public ChoosePlayerController(ChoosePlayerPage view, GameGateway gameGateway) {
        this.view = view;
        this.gameGateway = gameGateway;
        initButtons();
        refreshUi();
    }

    private void initButtons() {
        view.getAddPlayerButton().setOnAction(e -> addPlayer());
        view.getContinueButton().setOnAction(e -> confirm());
        view.getCancelButton().setOnAction(e -> cancel());
        view.getSavePlayerButton().setOnAction(e -> savePlayers());
        view.getLoadPlayersButton().setOnAction(e -> loadPlayers());
    }

    private void addPlayer() {
      String name = view.getNameField().getText().trim();
      String token = view.getSelectedToken();
      LocalDate birthday = view.getBirthdayPicker().getValue();
      boolean invalid = name.isEmpty()
                      || birthday == null || birthday.isAfter(LocalDate.now())
                      || token == null || token.isEmpty();

      if (invalid) {
          alert("Please choose a valid name, birthday and token.");
          return;
      }

      gameGateway.addPlayer(name, token, birthday);
      view.getNameField().clear();
      view.disableToken(token);
      refreshUi();
  }

    private List<PlayerView> sortPlayersByBirthday() {
        return gameGateway.players().stream()
                 .sorted(Comparator.comparing(PlayerView::birthday).reversed())
                 .toList();
    }
    
    private void setFirstPlayer() {
        if (!gameGateway.players().isEmpty()) {
            firstPlayerToken = sortPlayersByBirthday().getFirst().token();
            // TODO: tell gateway / board whose turn it is
        }
    }

    private void displayPlayersByAge() {
        view.getAddedPlayersBox().getChildren().setAll(
            sortPlayersByBirthday().stream()
                                   .map(this::playerBox)
                                   .toList());
    }
  
  private VBox playerBox(PlayerView pv) {
    int years = java.time.Period.between(pv.birthday(), LocalDate.now()).getYears();

    Label name = new Label(pv.name());
    name.getStyleClass().add("player-name");

    Label age  = new Label(years + " yrs");
    age.getStyleClass().add("player-age");

    VBox box = new VBox(name, age);
    box.setAlignment(javafx.geometry.Pos.CENTER);
    box.setSpacing(2);
    return box;
  }

    private void savePlayers() {
      FileChooser fc = new FileChooser();
      fc.setTitle("Save players");
      fc.setInitialFileName("players.csv");
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

      File out = fc.showSaveDialog(view.getAddPlayerButton().getScene().getWindow());
      if (out == null) return;

      try {
          List<String[]> rows = gameGateway.players().stream()
                                   .map(p -> new String[]{p.name(), p.token(), p.birthday().toString()})
                                   .toList();
          PlayerCsv.save(rows, out.toPath());
      } catch (IOException ex) {
          alert("Could not save players: " + ex.getMessage());
      }
  }

  private void loadPlayers() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load players");
    fileChooser.getExtensionFilters()
      .add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

    File in = fileChooser.showOpenDialog(view.getAddPlayerButton().getScene().getWindow());
    if (in == null) return;

    try {
        List<String[]> rows = PlayerCsv.load(in.toPath());

        LoadedPlayersPage loadedPlayersPage = new LoadedPlayersPage(rows);
        Stage pop = createModalPopup("Players", loadedPlayersPage.getView(), 550, 450);

        loadedPlayersPage.getaddSelectedButton().setOnAction(ev -> {
            gameGateway.loadPlayers(loadedPlayersPage.getSelectedRows());  // only chosen ones
            refreshUi();
            pop.close();
        });
        loadedPlayersPage.getCancelButton().setOnAction(ev -> pop.close());

        pop.showAndWait();

    } catch (IOException ex) {
        alert("Could not load players: " + ex.getMessage());
    }
}

  private void refreshUi() {
    // sort descending by birthday  (oldest first → starts)
    List<PlayerView> sorted = gameGateway.players().stream()
                                 .sorted(Comparator.comparing(PlayerView::birthday).reversed())
                                 .toList();

    // highlight first turn token (if needed elsewhere you can store it)
    if (!sorted.isEmpty()) {
        String firstToken = sorted.getFirst().token();
        // gw.setFirstTurn(firstToken)   // ← optional future API call
    }

    // rebuild the visible list
    view.getAddedPlayersBox().getChildren().setAll(
            sorted.stream()
                  .map(this::nameLabel)
                  .collect(Collectors.toList())
    );

    // grey-out already taken tokens
    sorted.forEach(pv -> view.disableToken(pv.token()));
  }

    private Stage createModalPopup(String title, javafx.scene.Parent root, int width, int height) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        Scene scene = new Scene(root, width, height);
        
        scene.getRoot().requestFocus();

        popupStage.setScene(scene);
        return popupStage;
    }

    private VBox nameLabel(PlayerView pv) {
        Label lbl = new Label(pv.name());
        lbl.getStyleClass().add("player-name");
        VBox box  = new VBox(lbl);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        return box;
    }

    @Override public void confirm() { 
        close(); 
    }
    @Override public void cancel()  { 
        close(); 
    }

  private void close() {
    ((Stage) view.getContinueButton().getScene().getWindow()).close();
  }

  private static void alert(String message) {
    new Alert(Alert.AlertType.WARNING, message).showAndWait();
  }
    
}
