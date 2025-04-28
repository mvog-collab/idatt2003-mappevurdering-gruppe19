package edu.ntnu.idatt2003.gateway;

public record PlayerView(
        String  name,
        String  token,   // "BLUE", "GREEN" …
        int     tileId,
        boolean hasTurn) {}
