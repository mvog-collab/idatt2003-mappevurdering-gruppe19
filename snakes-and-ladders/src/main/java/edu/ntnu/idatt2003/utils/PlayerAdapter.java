package edu.ntnu.idatt2003.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import edu.games.engine.Player;
import edu.games.engine.Token;
import edu.ntnu.idatt2003.dto.PlayerDTO;

public final class PlayerAdapter {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    private PlayerAdapter() { }

    public static PlayerDTO toDto(Player player) {
        return new PlayerDTO(
                player.getName(),
                player.getToken().name(),
                dateFormatter.format(player.getBirtday())
        );
    }

    public static Player fromDto(PlayerDTO dto) {
        return new Player(
                dto.name(),
                Token.valueOf(dto.token()),
                LocalDate.parse(dto.birthday(), dateFormatter)
        );
    }
}
