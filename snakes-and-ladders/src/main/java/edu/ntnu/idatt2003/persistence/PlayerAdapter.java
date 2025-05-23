package edu.ntnu.idatt2003.persistence;

import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.ntnu.idatt2003.model.dto.PlayerDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Adapter for converting between game-engine {@link Player} and
 * {@link PlayerDTO}.
 * <p>
 * Handles formatting and parsing of birthday dates in ISO_LOCAL_DATE format.
 * </p>
 */
public class PlayerAdapter {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

  private PlayerAdapter() {
    // Prevent instantiation
  }

  /**
   * Converts a {@link Player} to its data-transfer object representation.
   *
   * @param player the game-engine player instance
   * @return a {@link PlayerDTO} with name, token name, and formatted birthday
   */
  public static PlayerDTO toDto(Player player) {
    return new PlayerDTO(
        player.getName(),
        player.getToken().name(),
        dateFormatter.format(player.getBirthday()));
  }

  /**
   * Constructs a game-engine {@link Player} from its DTO.
   *
   * @param dto the player DTO containing name, token, and birthday string
   * @return a new {@link Player} instance
   */
  public static Player fromDto(PlayerDTO dto) {
    return new Player(
        dto.playerName(),
        Token.valueOf(dto.playerToken()),
        LocalDate.parse(dto.birthday(), dateFormatter));
  }
}