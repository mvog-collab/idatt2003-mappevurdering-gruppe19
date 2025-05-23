package edu.ntnu.idatt2003.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2003.exception.JsonParsingException;
import edu.ntnu.idatt2003.exception.ResourceNotFoundException;
import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.model.dto.TileDTO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for loading classic Snakes & Ladders board data from JSON resources.
 * <p>
 * Reads a {@code BoardDTO} from the classpath, extracts snake and ladder
 * mappings, and returns a {@link BoardAdapter.MapData}.
 * </p>
 */
public class BoardFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Logger LOG = Logger.getLogger(BoardFactory.class.getName());

  private BoardFactory() {
    // Prevent instantiation
  }

  /**
   * Loads board configuration from a JSON file on the classpath.
   *
   * @param resourcePath the classpath location of the board JSON
   * @return a {@link BoardAdapter.MapData} containing board size and mappings
   * @throws ResourceNotFoundException if the resource cannot be found
   * @throws JsonParsingException      if an I/O or parsing error occurs
   */
  public static BoardAdapter.MapData loadFromClasspath(String resourcePath) {
    LOG.info("Loading board data from classpath resource: " + resourcePath);
    try (InputStream is = BoardFactory.class.getResourceAsStream(resourcePath)) {
      if (is == null) {
        LOG.log(Level.SEVERE, "Resource not found: " + resourcePath);
        throw new ResourceNotFoundException(resourcePath);
      }

      BoardDTO dto = MAPPER.readValue(is, BoardDTO.class);
      Map<Integer, Integer> snakes = new HashMap<>();
      Map<Integer, Integer> ladders = new HashMap<>();

      for (TileDTO tile : dto.tiles()) {
        if (tile.snakeTo() != null) {
          snakes.put(tile.tileId(), tile.snakeTo());
        }
        if (tile.ladderTo() != null) {
          ladders.put(tile.tileId(), tile.ladderTo());
        }
      }

      LOG.info("Successfully loaded and parsed board data from: " + resourcePath);
      return new BoardAdapter.MapData(dto.boardSize(), snakes, ladders);

    } catch (IOException e) {
      LOG.log(Level.SEVERE,
          "Failed to read or parse board JSON from resource: " + resourcePath, e);
      throw new JsonParsingException(
          "Failed to read or parse board JSON from resource: " + resourcePath, e);
    }
  }
}