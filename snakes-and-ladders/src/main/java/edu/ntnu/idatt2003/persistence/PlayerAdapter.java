package edu.ntnu.idatt2003.persistence;

import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.ntnu.idatt2003.model.dto.PlayerDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class PlayerAdapter {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

  private PlayerAdapter() {}

  public static PlayerDTO toDto(Player player) {
    return new PlayerDTO(
        player.getName(), player.getToken().name(), dateFormatter.format(player.getBirthday()));
  }

  public static Player fromDto(PlayerDTO dto) {
    return new Player(
        dto.playerName(), Token.valueOf(dto.playerToken()), LocalDate.parse(dto.birthday(), dateFormatter));
  }
}
