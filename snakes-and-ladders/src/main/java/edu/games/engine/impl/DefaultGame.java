package edu.games.engine.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import edu.games.engine.board.Board;
import edu.games.engine.board.LinearTile;
import edu.games.engine.board.Tile;
import edu.games.engine.dice.Dice;
import edu.games.engine.model.Game;
import edu.games.engine.model.Player;
import edu.games.engine.rule.RuleEngine;
import edu.ntnu.idatt2003.utils.Log;

public final class DefaultGame implements Game {

    private final Board board;
    private final RuleEngine rules;
    private final Dice dice;
    private final List<Player> players;
    private int currentIndex = 0;
    private Player winner;

    public DefaultGame(Board board, RuleEngine rules, List<Player> players, Dice dice) { 
        this.board = Objects.requireNonNull(board);
        this.rules = Objects.requireNonNull(rules);
        this.dice = Objects.requireNonNull(dice);
        this.players = new ArrayList<>(players);
        players.forEach(p -> p.moveTo(board.start()));
    }

    @Override 
    public int playTurn() {
        if (winner != null) return 0;                // already finished
        Player currentPlayer = currentPlayer();
    
        int rolledValue = dice.roll();
        Tile destinationTile = board.move(currentPlayer.getCurrentTile(), rolledValue);
        Log.game().info(() ->
            "%s moves from %d to %d"
            .formatted(currentPlayer.getName(),
                      currentPlayer.getCurrentTile().id(),
                      ((LinearTile) destinationTile).id()));
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

      public void setCurrentPlayerIndex(int idx) {
        if (players.isEmpty()) return;
        currentIndex = Math.floorMod(idx, players.size());
      }

      public Board board() {
        return board;
      }

      public Dice dice() {
        return dice;
      }
}