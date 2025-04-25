package edu.games.engine;

import java.util.List;
import java.util.Optional;

public interface Game {
    int playTurn();
    Player currentPlayer();
    Optional<Player> winner();
    List<Player> players();
}
