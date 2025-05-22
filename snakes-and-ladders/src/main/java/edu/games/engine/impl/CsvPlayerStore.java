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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class CsvPlayerStore implements PlayerStore {

  private static final String SEP = ",";
  private static final Logger LOG = Logger.getLogger(CsvPlayerStore.class.getName());

  @Override
  public void savePlayers(List<Player> players, Path out) throws StorageException {
    if (players == null || players.isEmpty()) {
      LOG.warning("Attempted to save null or empty player list.");
      throw new ValidationException("Players list is null or empty.");
    }
    if (out == null) {
      LOG.warning("Attempted to save players with null output path.");
      throw new ValidationException("Output path is null.");
    }
    LOG.info(() -> "Saving " + players.size() + " players to CSV: " + out.toString());
    try (BufferedWriter w = Files.newBufferedWriter(out)) {
      for (Player p : players) {
        w.write("%s%s%s%s%s%n".formatted(
            p.getName(), SEP, p.getToken(), SEP, p.getBirthday()));
      }
      LOG.info("Successfully saved players to " + out.toString());
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Could not save players to " + out.toString(), e);
      throw new StorageException("Could not save players to " + out.toString(), e);
    }
  }

  @Override
  public List<Player> loadPlayers(Path in) throws StorageException {
    if (in == null) {
      LOG.warning("Attempted to load players with null input path.");
      throw new ValidationException("Input path is null.");
    }
    LOG.info("Loading players from CSV: " + in.toString());
    List<Player> list = new ArrayList<>();
    try (BufferedReader r = Files.newBufferedReader(in)) {
      String line;
      int lineNumber = 0;
      while ((line = r.readLine()) != null) {
        lineNumber++;
        String[] parts = line.split(SEP);
        if (parts.length >= 3) {
          try {
            String name = parts[0];
            Token token = Token.valueOf(parts[1].trim().toUpperCase());
            LocalDate birthday = LocalDate.parse(parts[2].trim());
            list.add(new Player(name, token, birthday));
          } catch (IllegalArgumentException | DateTimeParseException e) {
            LOG.log(Level.WARNING, "Skipping malformed line " + lineNumber + " in " + in + ": " + line, e);
          }
        } else {
          LOG.warning("Skipping malformed line " + lineNumber + " (not enough parts) in " + in + ": " + line);
        }
      }
      LOG.info(() -> "Successfully loaded " + list.size() + " players from " + in.toString());
      return list;
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Could not load players from " + in.toString(), e);
      throw new StorageException("Could not load players from " + in.toString(), e);
    }
  }
}