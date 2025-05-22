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

  private final EventType typeOfEvent;
  private final Object data;

  public BoardGameEvent(EventType typeOfEvent, Object data) {
    this.typeOfEvent = typeOfEvent;
    this.data = data;
  }

  public EventType getTypeOfEvent() {
    return typeOfEvent;
  }

  public Object getData() {
    return data;
  }
}
