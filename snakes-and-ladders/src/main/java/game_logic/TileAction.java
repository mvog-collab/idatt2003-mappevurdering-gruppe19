package game_logic;

import models.Player;

public interface TileAction {

  void applyAction(Player player);
  int getActionPosition();
}
