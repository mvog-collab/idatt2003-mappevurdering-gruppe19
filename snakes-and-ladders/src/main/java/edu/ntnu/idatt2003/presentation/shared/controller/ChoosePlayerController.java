package edu.ntnu.idatt2003.presentation.shared.controller;

import edu.ntnu.idatt2003.exception.CsvOperationException;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.presentation.common.controller.AbstractPopupController;
import edu.ntnu.idatt2003.presentation.shared.view.ChoosePlayerPage;
import edu.ntnu.idatt2003.presentation.shared.view.LoadedPlayersPage;
import edu.ntnu.idatt2003.utils.Dialogs;
import edu.ntnu.idatt2003.utils.Errors;
import edu.ntnu.idatt2003.utils.UiDialogs;
import edu.ntnu.idatt2003.utils.csv.PlayerCsv;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for managing player selection and configuration operations.
 * <p>
 * Handles adding players, saving/loading player data to/from CSV files,
 * and coordinating between the player selection view and game gateway.
 * </p>
 */
public class ChoosePlayerController extends AbstractPopupController<ChoosePlayerPage> {

  /** Logger for this controller */
  private static final Logger LOG = Logger.getLogger(ChoosePlayerController.class.getName());

  /**
   * Constructs a new ChoosePlayerController with the specified view and gateway.
   *
   * @param view    the player selection view to control
   * @param gateway the game gateway for player management
   */
  public ChoosePlayerController(ChoosePlayerPage view, CompleteBoardGame gateway) {
    super(view, gateway);
    LOG.info("ChoosePlayerController initialized.");
    view.updatePlayerDisplay(gateway.players());
  }

  /**
   * Initializes event handlers for UI interactions.
   */
  @Override
  protected void initializeEventHandlers() {
    view.getAddPlayerButton().setOnAction(e -> addPlayer());
    view.getContinueButton().setOnAction(e -> confirm());
    view.getCancelButton().setOnAction(e -> cancel());
    view.getSavePlayerButton().setOnAction(e -> savePlayers());
    view.getLoadPlayersButton().setOnAction(e -> loadPlayers());
  }

  /**
   * Adds a new player based on current form input.
   * <p>
   * Validates input fields and adds the player to the gateway if valid.
   * </p>
   */
  private void addPlayer() {
    LOG.fine("Attempting to add player.");
    String name = view.getNameField().getText().trim();
    String token = view.getSelectedToken();
    LocalDate birthday = view.getBirthdayPicker().getValue();
    boolean invalid = name.isEmpty()
        || birthday == null
        || birthday.isAfter(LocalDate.now())
        || token == null
        || token.isEmpty();

    if (invalid) {
      LOG.warning("Invalid player setup data provided.");
      alert("Invalid setup", "Please choose a valid name, birthday and token.");
      return;
    }

    try {
      gateway.addPlayer(name, token, birthday);
      LOG.info("Player added: " + name + " with token " + token);
      view.getNameField().clear();
      view.disableToken(token);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error adding player " + name, ex);
      Errors.handle("Could not add player: " + ex.getMessage(), ex);
    }
  }

  /**
   * Saves current players to a CSV file selected by the user.
   */
  private void savePlayers() {
    LOG.info("Save players button clicked. Opening file chooser.");
    FileChooser fc = new FileChooser();
    fc.setTitle("Save players");
    fc.setInitialFileName("players.csv");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

    File out = fc.showSaveDialog(view.getAddPlayerButton().getScene().getWindow());
    if (out == null) {
      LOG.info("Save players dialog cancelled by user.");
      return;
    }

    LOG.info("Attempting to save players to: " + out.getAbsolutePath());
    try {
      List<String[]> rows = gateway.players().stream()
          .map(p -> new String[] { p.playerName(), p.playerToken(), p.birthday().toString() })
          .toList();
      PlayerCsv.save(rows, out.toPath());
      LOG.info("Players saved successfully to: " + out.getAbsolutePath());
      Dialogs.info("Players Saved", "Players saved to " + out.getName());
    } catch (CsvOperationException ex) {
      LOG.log(Level.WARNING, "Could not save players to " + out.getAbsolutePath(), ex);
      alert("Save Error", "Could not save players: " + ex.getMessage());
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Unexpected error saving players to " + out.getAbsolutePath(), ex);
      Errors.handle("An unexpected error occurred while saving players.", ex);
    }
  }

  /**
   * Loads players from a CSV file and displays selection dialog.
   */
  private void loadPlayers() {
    LOG.info("Load players button clicked. Opening file chooser.");
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load players");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

    File in = fileChooser.showOpenDialog(view.getAddPlayerButton().getScene().getWindow());
    if (in == null) {
      LOG.info("Load players dialog cancelled by user.");
      return;
    }

    LOG.info("Attempting to load players from: " + in.getAbsolutePath());
    try {
      List<String[]> rows = PlayerCsv.load(in.toPath());
      LOG.info("Players data loaded from file: " + in.getAbsolutePath() + ". Found " + rows.size() + " entries.");

      LoadedPlayersPage loadedPlayersPage = new LoadedPlayersPage(rows);
      Stage popup = UiDialogs.createModalPopup("Players", loadedPlayersPage.getView(), 650, 550);

      loadedPlayersPage
          .getAddSelectedButton()
          .setOnAction(
              ev -> {
                List<String[]> selectedRows = loadedPlayersPage.getSelectedRows();
                LOG.info("Adding " + selectedRows.size() + " selected players from loaded file.");
                gateway.clearPlayers();
                gateway.loadPlayers(selectedRows);
                popup.close();
                LOG.info("Selected players added and popup closed.");
              });
      loadedPlayersPage.getCancelButton().setOnAction(ev -> {
        LOG.info("Load players from file popup cancelled.");
        popup.close();
      });

      popup.showAndWait();
    } catch (CsvOperationException ex) {
      LOG.log(Level.WARNING, "Could not load players from file " + in.getAbsolutePath(), ex);
      Errors.handle("Could not load players from file: " + ex.getMessage(), ex);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Unexpected error loading players from " + in.getAbsolutePath(), ex);
      Errors.handle("An unexpected error occurred while loading players.", ex);
    }
  }

  /**
   * Confirms the player selection and closes the dialog.
   */
  @Override
  public void confirm() {
    LOG.info("ChoosePlayer dialog confirmed.");
    close(view.getContinueButton());
  }

  /**
   * Cancels player selection, clears all players, and closes the dialog.
   */
  @Override
  public void cancel() {
    gateway.clearPlayers();
    LOG.info("ChoosePlayer dialog cancelled and players cleared.");
    close(view.getCancelButton());
  }

  /**
   * Displays a warning alert with the specified header and message.
   *
   * @param header  the alert header text
   * @param message the alert message text
   */
  private static void alert(String header, String message) {
    Dialogs.warn(header, message);
  }
}