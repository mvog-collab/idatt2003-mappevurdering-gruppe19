package edu.ntnu.idatt2003.presentation.common.view;

import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 * Interface defining the contract for a game screen view.
 * <p>
 * Extends {@link BoardGameObserver} to receive game events and
 * provides methods for updating UI elements like dice, players,
 * and announcing a winner.
 * </p>
 */
public interface GameView extends BoardGameObserver {

  /** @return the button used to roll the dice */
  Button getRollButton();

  /** @return the button used to start a new game */
  Button getPlayAgainButton();

  /** Disables the roll button to prevent multiple clicks */
  void disableRollButton();

  /** Enables the roll button when appropriate */
  void enableRollButton();

  /**
   * Displays the winner’s name prominently in the UI.
   *
   * @param name the winning player’s name
   */
  void announceWinner(String name);

  /**
   * Sets the current list of players and their overlay parameters.
   *
   * @param players  list of {@link PlayerView} instances
   * @param overlays corresponding list of {@link OverlayParams}
   */
  void setPlayers(List<PlayerView> players, List<OverlayParams> overlays);

  /**
   * Animates or displays the dice roll values.
   *
   * @param values the rolled dice value(s)
   */
  void showDice(int values);

  /**
   * Connects this view to the game model for receiving events
   * and invoking actions.
   *
   * @param gateway the {@link GameGateway} instance
   */
  void connectToModel(GameGateway gateway);

  /** @return the JavaFX {@link Scene} for this view */
  Scene getScene();
}