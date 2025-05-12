package edu.ntnu.idatt2003.ui.service.player;

import edu.ntnu.idatt2003.gateway.view.PlayerView;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public interface PlayerUIService {
  Node createPlayerBox(PlayerView player, boolean hasTurn);

  ImageView createTokenImage(String tokenName);

  void updateTurnIndicator(Node playerBox, boolean hasTurn);

  List<ImageView> createPlayerPieces(PlayerView player);

  void updatePlayerDisplay(Node container, List<PlayerView> players);
}
