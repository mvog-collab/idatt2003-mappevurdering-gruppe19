package edu.ntnu.idatt2003.gateway;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface PlayerManagement extends GameGateway {
    void addPlayer(String name, String token, LocalDate birthday);
    void loadPlayers(List<String[]> rows);
    void savePlayers(Path out) throws IOException;
}