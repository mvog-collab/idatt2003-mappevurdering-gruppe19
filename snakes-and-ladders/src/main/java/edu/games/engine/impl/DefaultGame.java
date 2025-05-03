package edu.games.engine.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import edu.games.engine.board.Board;
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
      if (winner != null) return 0;

      Player currentPlayer = currentPlayer();
      int rolledValue = dice.roll();

      int moveSteps = (rolledValue == 12) ? 0 : rolledValue;

      if (moveSteps > 0) {
          Tile destinationTile = board.move(currentPlayer.getCurrentTile(), rolledValue);
          if (destinationTile != null) {
            currentPlayer.moveTo(destinationTile);
          }
      }

      boolean extraTurn = rules.apply(board, currentPlayer, dice.lastValues());

      bumpIfOccupied(currentPlayer);

      if (!extraTurn) {
          currentIndex = (currentIndex + 1) % players.size();
      }

      if (board.isEnd(currentPlayer.getCurrentTile())) {
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

      private void bumpIfOccupied(Player moved) {
        Tile dest = moved.getCurrentTile();

        if (dest == null) return;
    
        players.stream()
               .filter(p -> p != moved)
               .filter(p -> p.getCurrentTile() != null && dest.equals(p.getCurrentTile()))
               .forEach(p -> {
                   p.moveTo(board.start());
                   Log.game().info(() -> 
                       "%s bumps %s back to start"
                       .formatted(moved.getName(), p.getName()));
               });
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