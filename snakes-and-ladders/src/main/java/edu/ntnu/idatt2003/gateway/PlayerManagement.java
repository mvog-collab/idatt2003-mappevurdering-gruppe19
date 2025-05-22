package edu.ntnu.idatt2003.gateway;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface PlayerManagement extends GameGateway {
  void addPlayer(String playerName, String playerToken, LocalDate birthday);

  void loadPlayers(List<String[]> rows);

  void clearPlayers();

  void savePlayers(Path out);
}
