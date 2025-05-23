package edu.ntnu.idatt2003.gateway;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Defines operations for managing players in the game,
 * including adding, loading, clearing, and saving player data.
 * <p>
 * Extends {@link GameGateway} so observers can react to changes.
 */
public interface PlayerManagement extends GameGateway {

  /**
   * Adds a new player to the current game.
   *
   * @param playerName  the display name of the new player
   * @param playerToken the token identifier (e.g., color) for the player
   * @param birthday    the player's birth date for record-keeping
   */
  void addPlayer(String playerName, String playerToken, LocalDate birthday);

  /**
   * Loads multiple players from raw string data rows.
   * Each row must have at least three elements: name, token, and birthday string.
   *
   * @param rows a list of string arrays representing player data
   */
  void loadPlayers(List<String[]> rows);

  /**
   * Removes all players from the current game.
   */
  void clearPlayers();

  /**
   * Persists the current player list to the given file path.
   *
   * @param out the path to write the player CSV data
   */
  void savePlayers(Path out);
}
