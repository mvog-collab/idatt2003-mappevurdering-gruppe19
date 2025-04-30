package edu.games.engine.rule;

import java.util.Map;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearBoard;
import edu.games.engine.model.Player;
import edu.ntnu.idatt2003.utils.Log;

public final class SnlRuleEngine implements RuleEngine {

    private final Map<Integer,Integer> snakes;
    private final Map<Integer,Integer> ladders;
    private final RuleConfig.ExtraTurnPolicy extraTurn;
  
    public SnlRuleEngine(Map<Integer,Integer> snakes,
                         Map<Integer,Integer> ladders,
                         RuleConfig.ExtraTurnPolicy extraTurn) {
      this.snakes = snakes;
      this.ladders = ladders;
      this.extraTurn = extraTurn;
    }
  
    @Override
    public boolean apply(Board board, Player player, int rolled) {
      int playerPosition = player.getCurrentTile().id();
      Integer tail = snakes.get(playerPosition);
      Integer top  = ladders.get(playerPosition);
      if (tail != null || top != null) {
        int dest = tail != null ? tail : top;
        String type = tail != null ? "snake" : "ladder";

        Log.rules().info(() ->
            "%s hits a %s: %d -> %d"
            .formatted(player.getName(), type, playerPosition, dest));
        LinearBoard linearBoard = (LinearBoard) board;              // safe: SnL uses linear
        player.moveTo(linearBoard.tile(dest));
      }
  
      return switch (extraTurn) {
        case NONE            -> false;
        case EVEN_BUT_NOT_12 -> rolled % 2 == 0 && rolled != 12;
        case ON_SIX          -> rolled == 6;
      };
    }
  }