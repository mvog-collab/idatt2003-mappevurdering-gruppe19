package edu.ntnu.idatt2003.ui.common.view;

import edu.games.engine.observer.BoardGameObserver;
import edu.ntnu.idatt2003.gateway.GameGateway;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public interface GameView extends BoardGameObserver {
  Button getRollButton();

  Button getPlayAgainButton();

  void disableRollButton();

  void enableRollButton();

  void announceWinner(String name);

  void setPlayers(List<PlayerView> players, List<OverlayParams> overlays);

  void showDice(int values);

  void connectToModel(GameGateway gateway);

  Scene getScene();
}
