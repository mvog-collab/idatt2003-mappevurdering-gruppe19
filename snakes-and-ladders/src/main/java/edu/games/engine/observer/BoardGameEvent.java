package edu.games.engine.observer;

/**
 * Represents an event that occurs during a board game.
 * Each event has a specific type and optional associated data.
 */
public class BoardGameEvent {

  /**
   * Enum representing the various types of events that can occur in a board game.
   */
  public enum EventType {
    /** A player has moved a piece */
    PLAYER_MOVED,

    /** A die or dice has been rolled */
    DICE_ROLLED,

    /** The game has started */
    GAME_STARTED,

    /** The game has been reset */
    GAME_RESET,

    /** A winner has been declared */
    WINNER_DECLARED,

    /** A new player has been added */
    PLAYER_ADDED,

    /** The turn has changed to another player */
    TURN_CHANGED,

    /** Players have been loaded from a file or resource */
    PLAYERS_LOADED,

    /** A game piece has been selected (e.g., for movement) */
    PIECE_SELECTED
  }

  private final EventType typeOfEvent;
  private final Object data;

  /**
   * Constructs a new BoardGameEvent with a specific type and optional data.
   *
   * @param typeOfEvent the type of the event
   * @param data        the data associated with the event (may be null)
   */
  public BoardGameEvent(EventType typeOfEvent, Object data) {
    this.typeOfEvent = typeOfEvent;
    this.data = data;
  }

  /**
   * Returns the type of event.
   *
   * @return the event type
   */
  public EventType getTypeOfEvent() {
    return typeOfEvent;
  }

  /**
   * Returns the data associated with this event.
   *
   * @return the event data, or null if no data is present
   */
  public Object getData() {
    return data;
  }
}
