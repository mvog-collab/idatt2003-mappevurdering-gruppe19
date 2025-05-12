package edu.ntnu.idatt2003.ui.shared.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.ui.common.controller.AbstractPopupController;
import edu.ntnu.idatt2003.ui.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.ui.shared.view.LoadedPlayersPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.Errors;
import edu.ntnu.idatt2003.utils.csv.PlayerCsv;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChoosePlayerController extends AbstractPopupController<ChoosePlayerPage> {

  public ChoosePlayerController(ChoosePlayerPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    // Display initial player list
    view.updatePlayerDisplay(gateway.players());
  }

  @Override
  protected void initializeEventHandlers() {
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
    boolean invalid =
        name.isEmpty()
            || birthday == null
            || birthday.isAfter(LocalDate.now())
            || token == null
            || token.isEmpty();

    if (invalid) {
      alert("Invalid setup", "Please choose a valid name, birthday and token.");
      return;
    }

    // Add player to gateway - model will notify observers
    gateway.addPlayer(name, token, birthday);

    // Clear input field and disable token button
    view.getNameField().clear();
    view.disableToken(token);
  }

  private void savePlayers() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Save players");
    fc.setInitialFileName("players.csv");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

    File out = fc.showSaveDialog(view.getAddPlayerButton().getScene().getWindow());
    if (out == null) return;

    try {
      List<String[]> rows =
          gateway.players().stream()
              .map(p -> new String[] {p.name(), p.token(), p.birthday().toString()})
              .toList();
      PlayerCsv.save(rows, out.toPath());
    } catch (IOException ex) {
      alert("Invalid player save", "Could not save players: " + ex.getMessage());
    }
  }

  private void loadPlayers() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load players");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

    File in = fileChooser.showOpenDialog(view.getAddPlayerButton().getScene().getWindow());
    if (in == null) return;

    try {
      List<String[]> rows = PlayerCsv.load(in.toPath());

      LoadedPlayersPage loadedPlayersPage = new LoadedPlayersPage(rows);
      Stage pop = createModalPopup("Players", loadedPlayersPage.getView(), 550, 450);

      loadedPlayersPage
          .getaddSelectedButton()
          .setOnAction(
              ev -> {
                gateway.loadPlayers(loadedPlayersPage.getSelectedRows());
                pop.close();
              });
      loadedPlayersPage.getCancelButton().setOnAction(ev -> pop.close());

      pop.showAndWait();
    } catch (IOException ex) {
      Errors.handle("Could not load players from file.", ex);
    }
  }

  @Override
  public void confirm() {
    close(view.getContinueButton());
  }

  @Override
  public void cancel() {
    close(view.getCancelButton());
  }

  private static void alert(String header, String message) {
    Dialogs.warn(header, message);
  }
}
