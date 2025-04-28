package edu.games.engine;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DefaultGame implements Game {

    private final Board board;
    private final RuleEngine rules;
    private final DiceService dice;
    private final List<Player> players;
    private int currentIndex = 0;
    private Player winner;

    public DefaultGame(Board board, RuleEngine rules, List<Player> players, int numberOfDice) { 
        this.board = Objects.requireNonNull(board);
        this.rules = Objects.requireNonNull(rules);
        this.dice = new DiceService(numberOfDice);
        this.players = List.copyOf(players);
        players.forEach(p -> p.moveTo(board.start()));
    }

    @Override 
    public int playTurn() {
        if (winner != null) return 0;                // already finished
        Player currentPlayer = currentPlayer();
    
        int rolledValue = dice.roll();
        Tile destinationTile = board.move(currentPlayer.getCurrentTile(), rolledValue);
        currentPlayer.moveTo(destinationTile);
    
        boolean extra = rules.apply(board, currentPlayer, rolledValue);
        if (!extra) {
          currentIndex = (currentIndex + 1) % players.size();
        }

        if (board.isEnd(destinationTile)) {
            winner = currentPlayer;
        }
        return rolledValue;
      }
    
      @Override 
      public Player currentPlayer() {
        return players.get(currentIndex);
      }
    
      @Override 
      public Optional<Player> winner() {
        return Optional.ofNullable(winner);
      }
    
      @Override 
      public List<Player> players() { 
        return players;
      }

      public Board board() {
        return board;
      }
}