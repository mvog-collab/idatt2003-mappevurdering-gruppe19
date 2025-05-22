package edu.games.engine.model;

import java.util.List;
import java.util.Optional;

public interface Game {
  int playTurn();

  Player currentPlayer();

  Optional<Player> getWinner();

  List<Player> getPlayers();
}
