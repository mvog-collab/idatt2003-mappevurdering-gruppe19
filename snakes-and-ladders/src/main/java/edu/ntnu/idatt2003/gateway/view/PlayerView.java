package edu.ntnu.idatt2003.gateway.view;

import java.time.LocalDate;

public record PlayerView(
        String  name,
        String  token,   // "BLUE", "GREEN" …
        int     tileId,
        LocalDate birthday,
        boolean hasTurn) {}
