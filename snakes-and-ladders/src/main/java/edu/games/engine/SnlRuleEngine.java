package edu.games.engine;

import java.util.Map;

public final class SnlRuleEngine implements RuleEngine {

    private final Map<Integer,Integer> snakes;
    private final Map<Integer,Integer> ladders;
  
    public SnlRuleEngine(Map<Integer,Integer> snakes,
                         Map<Integer,Integer> ladders) {
      this.snakes = snakes;
      this.ladders = ladders;
    }
  
    @Override
    public boolean apply(Board board, Player player, int rolled) {
      int playerPosition = player.getCurrentTile().id();
      Integer tail = snakes.get(playerPosition);
      Integer top  = ladders.get(playerPosition);
      if (tail != null || top != null) {
        int dest = tail != null ? tail : top;
        LinearBoard linearBoard = (LinearBoard) board;              // safe: SnL uses linear
        player.moveTo(linearBoard.tile(dest));
      }
  
      /* “pair but not 12 grants extra turn” rule */
      return rolled % 2 == 0 && rolled != 12;
    }
  }