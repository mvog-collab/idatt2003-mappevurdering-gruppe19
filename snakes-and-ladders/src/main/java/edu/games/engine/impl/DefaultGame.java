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
import edu.games.engine.strategy.GameStrategy;
import edu.ntnu.idatt2003.utils.Log;

public final class DefaultGame implements Game {

    private final Board board;
    private final RuleEngine rules;
    private final GameStrategy strategy;
    private final Dice dice;
    private final List<Player> players;
    private int currentIndex = 0;
    private Player winner;

    public DefaultGame(Board board, RuleEngine rules, GameStrategy strategy, List<Player> players, Dice dice) { 
        this.board = Objects.requireNonNull(board);
        this.rules = Objects.requireNonNull(rules);
        this.strategy = Objects.requireNonNull(strategy);
        this.dice = Objects.requireNonNull(dice);
        this.players = new ArrayList<>(players);
    }

  // In DefaultGame
  public int playTurn() {
      if (winner != null) return 0;

      Player currentPlayer = currentPlayer();
      int rolledValue = dice.roll();
      
      // Use strategy for game-specific rules
      GameStrategy strategy = getStrategy(); // You'd need to add this field
      
      // Let strategy determine if player can move and where
      Tile destinationTile = strategy.movePiece(currentPlayer, -1, rolledValue, this);
      if (destinationTile != null) {
          currentPlayer.moveTo(destinationTile);
          strategy.applySpecialRules(currentPlayer, null, destinationTile, this);
      }
      
      // Let strategy determine if player gets extra turn
      boolean extraTurn = strategy.processDiceRoll(currentPlayer, rolledValue, this);
      if (!extraTurn) {
          currentIndex = (currentIndex + 1) % players.size();
      }
      
      // Let strategy determine win condition  
      if (strategy.checkWinCondition(currentPlayer, this)) {
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

  public void setWinner(Player winner) {
    this.winner = winner;
  }

  public GameStrategy getStrategy() {
    return strategy;
  }

  public Board board() {
        return board;
      }

  public Dice dice() {
    return dice;
  }
}