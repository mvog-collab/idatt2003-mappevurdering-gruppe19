package edu.ntnu.idatt2003.utils.csv;

import edu.ntnu.idatt2003.exception.CsvOperationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerCsv {

  private static final Logger LOG = Logger.getLogger(PlayerCsv.class.getName());

  private PlayerCsv() {
  }

  public static void save(List<String[]> rows, Path out) throws CsvOperationException {
    LOG.info("Saving player data to CSV file: " + out.toString());
    try {
      Files.write(out, rows.stream().map(r -> String.join(",", r)).toList());
      LOG.info("Successfully saved player data to: " + out.toString());
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Failed to save player data to CSV: " + out.toString(), e);
      throw new CsvOperationException("Failed to save player data to CSV: " + out.toString(), e);
    }
  }

  public static List<String[]> load(Path in) throws CsvOperationException {
    LOG.info("Loading player data from CSV file: " + in.toString());
    try {
      List<String[]> data = Files.readAllLines(in).stream().map(l -> l.split(",")).toList();
      LOG.info("Successfully loaded player data from: " + in.toString());
      return data;
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Failed to load player data from CSV: " + in.toString(), e);
      throw new CsvOperationException("Failed to load player data from CSV: " + in.toString(), e);
    }
  }
}