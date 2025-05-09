package edu.ntnu.idatt2003.gateway;

import java.util.List;
import edu.ntnu.idatt2003.gateway.view.PlayerView;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;

public interface GameState extends GameGateway {
    boolean hasWinner();
    String currentPlayerName();
    int boardSize();
    List<OverlayParams> boardOverlays();
    List<PlayerView> players();
    List<Integer> lastDiceValues();
}