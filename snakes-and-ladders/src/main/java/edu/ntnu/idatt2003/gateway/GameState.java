package edu.ntnu.idatt2003.gateway;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;
import java.util.List;

public interface GameState extends GameGateway {
  boolean hasWinner();

  String currentPlayerName();

  int boardSize();

  List<OverlayParams> boardOverlays();

  List<PlayerView> players();

  List<Integer> lastDiceValues();
}
