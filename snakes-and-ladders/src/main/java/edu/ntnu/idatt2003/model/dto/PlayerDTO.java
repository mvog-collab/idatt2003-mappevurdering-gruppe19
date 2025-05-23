package edu.ntnu.idatt2003.model.dto;

/**
 * Data Transfer Object representing a player.
 * Used for serializing and deserializing player information.
 *
 * @param playerName  the display name of the player
 * @param playerToken the token identifier (e.g., "RED", "BLUE") for the player’s piece
 * @param birthday    the player’s birth date as an ISO-8601 string (yyyy-MM-dd)
 */
public record PlayerDTO(String playerName, String playerToken, String birthday) {
}
