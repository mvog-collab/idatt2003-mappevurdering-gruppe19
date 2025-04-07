package edu.ntnu.idatt2003.controllers;

import edu.ntnu.idatt2003.models.Player;
import java.time.LocalDate;

import edu.ntnu.idatt2003.models.GameModel;
import edu.ntnu.idatt2003.ui.ChoosePlayerPage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.stage.Stage;

public class ChoosePlayerController implements BasePopupController {

    private ChoosePlayerPage view;
    private GameModel gameModel;

    public ChoosePlayerController(ChoosePlayerPage view, GameModel gameModel) {
        this.view = view;
        this.gameModel = gameModel;
        init();
    }

    private void init() {
        view.getAddPlayerButton().setOnAction(e -> addPlayer());
        view.getContinueButton().setOnAction(e -> confirm());
        view.getCancelButton().setOnAction(e -> cancel());
    }

    private void addPlayer() {
        String name = view.getNameField().getText();
        LocalDate birthday = view.getBirthdayPicker().getValue();

        if (name == null || name.isBlank() || birthday == null || birthday.isAfter(LocalDate.now())) {
        return;
      }

      String dummyToken = "DummyToken";

      gameModel.addPlayer(name, dummyToken, birthday);
      view.getNameField().setText("");
      gameModel.setPlayersOfGame(sortPlayersByBirthday());
      view.getAddedPlayersBox().getChildren().add(view.displayPlayer(gameModel.getPlayers().getLast()));
    }

    private List<Player> sortPlayersByBirthday() {
        List<Player> players = new ArrayList<>(gameModel.getPlayers());
        players.sort(Comparator.comparing(Player::getBirthday).reversed());
        gameModel.setCurrentPlayer(players.get(0));
        return players;
    }

    @Override
    public void confirm() {
        if (gameModel.getPlayers().isEmpty()) {
            System.out.println("No players added!");
            return;
        }
        Stage stage = (Stage) view.getContinueButton().getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void cancel() {
        Stage stage = (Stage) view.getCancelButton().getScene().getWindow();
        stage.close();
    }
    
}
