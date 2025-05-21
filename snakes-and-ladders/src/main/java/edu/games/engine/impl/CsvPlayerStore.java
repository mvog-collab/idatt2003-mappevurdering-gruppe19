package edu.games.engine.impl;

import edu.games.engine.exception.ValidationException;
import edu.games.engine.exception.StorageException;
import edu.games.engine.model.Player;
import edu.games.engine.model.Token;
import edu.games.engine.store.PlayerStore;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CsvPlayerStore implements PlayerStore {

  private static final String SEP = ",";
  private static final Logger LOG = Logger.getLogger(CsvPlayerStore.class.getName());

  @Override
  public void save(List<Player> players, Path out) throws StorageException {
    if (players == null || players.isEmpty()) {
      throw new ValidationException("Players list is null or empty.");
    }
    if (out == null) {
      throw new ValidationException("Output path is null.");
    }
    try (BufferedWriter w = Files.newBufferedWriter(out)) {
      for (Player p : players) {
        w.write("%s%s%s%s%s%n".formatted(
            p.getName(), SEP, p.getToken(), SEP, p.getBirtday()));
      }
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Could not save players to " + out, e);
      throw new StorageException("Could not save players to " + out, e);
    }
  }

  @Override
  public List<Player> load(Path in) throws StorageException {
    if (in == null) {
      throw new ValidationException("Input path is null.");
    }
    try (BufferedReader r = Files.newBufferedReader(in)) {
    List<Player> list = new ArrayList<>();
      String line;
      while ((line = r.readLine()) != null) {
        String[] parts = line.split(SEP);
        list.add(new Player(parts[0], Token.valueOf(parts[1]), LocalDate.parse(parts[2])));
      }
      return list;
    } catch (IOException | RuntimeException e) {
      LOG.log(Level.WARNING, "Could not load players from " + in, e);
      throw new StorageException("Could not load players from " + in, e);
    }
  }
}
