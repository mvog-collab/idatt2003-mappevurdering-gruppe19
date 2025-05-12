package edu.games.engine.impl;

import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.store.PlayerStore;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CsvPlayerStore implements PlayerStore {

  private static final String SEP = ",";

  @Override
  public void save(List<Player> players, Path out) throws IOException {
    try (BufferedWriter w = new BufferedWriter(new FileWriter(out.toFile()))) {
      for (Player p : players) {
        w.write("%s%s%s%s%s%n".formatted(p.getName(), SEP, p.getToken(), SEP, p.getBirtday()));
      }
    }
  }

  @Override
  public List<Player> load(Path in) throws IOException {
    List<Player> list = new ArrayList<>();
    try (BufferedReader r = new BufferedReader(new FileReader(in.toFile()))) {
      String line;
      while ((line = r.readLine()) != null) {
        String[] parts = line.split(SEP);
        list.add(new Player(parts[0], Token.valueOf(parts[1]), LocalDate.parse(parts[2])));
      }
    }
    return list;
  }
}
