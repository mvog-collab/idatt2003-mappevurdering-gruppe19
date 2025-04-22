package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.ui.SettingsPage;
import edu.ntnu.idatt2003.utils.PlayerFileHandler;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SettingsController {

  private final SettingsPage view;
  private final GameModel gameModel;

  public SettingsController(SettingsPage view, GameModel gameModel) {
    this.view = view;
    this.gameModel = gameModel;
    init();
  }

  private void init() {
    view.getLoadPlayersButton().setOnAction(e -> loadPlayersFromFile());
    view.getSavePlayersButton().setOnAction(e -> savePlayersToFile());
    view.getBackButton().setOnAction(e -> closeWindow());
  }

  private void loadPlayersFromFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose players");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-files", "*.csv"));

    File file = fileChooser.showOpenDialog(view.getView().getScene().getWindow());

    if (file != null) {
      try {
        List<Player> players = PlayerFileHandler.loadPlayersFromCSV(file.getAbsolutePath());
        gameModel.setPlayersOfGame(players);
        gameModel.setCurrentPlayer(players.getFirst());
        for (Player player : players) {
          gameModel.setStartPosition(player);
        }

        new Alert(Alert.AlertType.INFORMATION, "Players loaded from file").showAndWait();

      } catch (IOException ex) {
        new Alert(Alert.AlertType.ERROR, "Could not load players: " + ex.getMessage()).showAndWait();
      }
    }
  }

  private void savePlayersToFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save players");
    fileChooser.setInitialFileName("Players_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-files", "*.csv"));

    File file = fileChooser.showSaveDialog(view.getView().getScene().getWindow());

    if (file != null) {
      try {
        PlayerFileHandler.savePlayersToCSV(gameModel.getPlayers(), file.getAbsolutePath());
        new Alert(Alert.AlertType.INFORMATION, "Players saved to file").showAndWait();
      } catch (IOException ex) {
        new Alert(Alert.AlertType.ERROR, "Could not save players: " + ex.getMessage()).showAndWait();
      }
    }
  }

  private void closeWindow() {
    Stage stage = (Stage) view.getBackButton().getScene().getWindow();
    stage.close();
  }
}
