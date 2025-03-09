package edu.ntnu.idatt2003.game_logic;

import edu.ntnu.idatt2003.models.Player;

public interface TileAction {

  void applyAction(Player player);
  int getActionPosition();
}
