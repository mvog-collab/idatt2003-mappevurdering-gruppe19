package edu.ntnu.idatt2003.ui.controller;
// package edu.ntnu.idatt2003.controllers;

// import edu.ntnu.idatt2003.ui.SettingsPage;
// import javafx.scene.control.Alert;
// import javafx.stage.FileChooser;
// import javafx.stage.Stage;

// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Path;
// import java.util.List;

// public class SettingsController {

//   private final SettingsPage view;

//   public SettingsController(SettingsPage view) {
//     this.view = view;
//     init();
//   }

//   private void init() {
//     view.getLoadPlayersButton().setOnAction(e -> loadPlayersFromFile());
//     view.getSavePlayersButton().setOnAction(e -> savePlayersToFile());
//     view.getBackButton().setOnAction(e -> closeWindow());
//   }

//   private void loadPlayersFromFile() {
//     FileChooser fileChooser = new FileChooser();
//     fileChooser.setTitle("Choose players");
//     fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-files", "*.csv"));

//     File file = fileChooser.showOpenDialog(view.getView().getScene().getWindow());
//     Path in = fileChooser.showOpenDialog(view.getLoadPlayersButton().getScene().getWindow()).toPath();

//     if (file != null) {
//       try {
//         List<Player> players = playerFileHandler.load(in);
//         gameModel.setPlayersOfGame(players);
//         gameModel.setCurrentPlayer(players.getFirst());
//         for (Player player : players) {
//           gameModel.setStartPosition(player);
//         }

//         new Alert(Alert.AlertType.INFORMATION, "Players loaded from file").showAndWait();

//       } catch (IOException ex) {
//         new Alert(Alert.AlertType.ERROR, "Could not load players: " + ex.getMessage()).showAndWait();
//       }
//     }
//   }

//   private void savePlayersToFile() {
//     FileChooser fileChooser = new FileChooser();
//     fileChooser.setTitle("Save players");
//     fileChooser.setInitialFileName("Players_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv");
//     fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-files", "*.csv"));

//     File file = fileChooser.showSaveDialog(view.getView().getScene().getWindow());
//     Path out = fileChooser.showOpenDialog(view.getSavePlayersButton().getScene().getWindow()).toPath();

//     if (file != null) {
//       try {
//         playerFileHandler.save(gameModel.getPlayers(), out);
//         new Alert(Alert.AlertType.INFORMATION, "Players saved to file").showAndWait();
//       } catch (IOException ex) {
//         new Alert(Alert.AlertType.ERROR, "Could not save players: " + ex.getMessage()).showAndWait();
//       }
//     }
//   }

//   private void closeWindow() {
//     Stage stage = (Stage) view.getBackButton().getScene().getWindow();
//     stage.close();
//   }
// }
