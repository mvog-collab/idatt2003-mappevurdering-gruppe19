package edu.games.engine.observer;

public class BoardGameEvent {
  public enum EventType {
    PLAYER_MOVED,
    DICE_ROLLED,
    GAME_STARTED,
    GAME_RESET,
    WINNER_DECLARED,
    PLAYER_ADDED,
    TURN_CHANGED,
    PLAYERS_LOADED,
    PIECE_SELECTED
  }

  private final EventType type;
  private final Object data;

  public BoardGameEvent(EventType type, Object data) {
    this.type = type;
    this.data = data;
  }

  public EventType getType() {
    return type;
  }

  public Object getData() {
    return data;
  }
}
