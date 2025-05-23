package edu.ntnu.idatt2003.utils.csv;

import edu.ntnu.idatt2003.exception.CsvOperationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading and writing player data in CSV format.
 * <p>
 * Provides methods to save a list of string arrays to a file
 * and to load them back into a {@code List<String[]>}.
 * </p>
 */
public final class PlayerCsv {

  private static final Logger LOG = Logger.getLogger(PlayerCsv.class.getName());

  private PlayerCsv() {
    // Prevent instantiation
  }

  /**
   * Writes all rows to the specified CSV file.
   *
   * @param rows a list of record arrays, each {@code String[]} representing a row
   * @param out  the path to the output file
   * @throws CsvOperationException if an I/O error occurs during writing
   */
  public static void save(List<String[]> rows, Path out) throws CsvOperationException {
    LOG.info("Saving player data to CSV file: " + out);
    try {
      Files.write(out, rows.stream()
          .map(r -> String.join(",", r))
          .toList());
      LOG.info("Successfully saved player data to: " + out);
    } catch (IOException e) {
      LOG.log(Level.WARNING,
          "Failed to save player data to CSV: " + out, e);
      throw new CsvOperationException(
          "Failed to save player data to CSV: " + out, e);
    }
  }

  /**
   * Reads all lines from the specified CSV file and splits them on commas.
   *
   * @param in the path to the input file
   * @return a list of record arrays, each {@code String[]} representing a row
   * @throws CsvOperationException if an I/O error occurs during reading
   */
  public static List<String[]> load(Path in) throws CsvOperationException {
    LOG.info("Loading player data from CSV file: " + in);
    try {
      List<String[]> data = Files.readAllLines(in).stream()
          .map(line -> line.split(","))
          .toList();
      LOG.info("Successfully loaded player data from: " + in);
      return data;
    } catch (IOException e) {
      LOG.log(Level.WARNING,
          "Failed to load player data from CSV: " + in, e);
      throw new CsvOperationException(
          "Failed to load player data from CSV: " + in, e);
    }
  }
}