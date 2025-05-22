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

public final class BoardFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Logger LOG = Logger.getLogger(BoardFactory.class.getName());

  private BoardFactory() {
  }

  public static BoardAdapter.MapData loadFromClasspath(String resourcePath) {
    LOG.info("Loading board data from classpath resource: " + resourcePath);
    try (InputStream inputStream = BoardFactory.class.getResourceAsStream(resourcePath)) {

      if (inputStream == null) {
        LOG.log(Level.SEVERE, "Resource not found: " + resourcePath);
        throw new ResourceNotFoundException(resourcePath);
      }

      BoardDTO dto = MAPPER.readValue(inputStream, BoardDTO.class);

      Map<Integer, Integer> snakes = new HashMap<>();
      Map<Integer, Integer> ladders = new HashMap<>();

      for (TileDTO tile : dto.tiles()) {
        if (tile.snakeTo() != null)
          snakes.put(tile.id(), tile.snakeTo());
        if (tile.ladderTo() != null)
          ladders.put(tile.id(), tile.ladderTo());
      }
      LOG.info("Successfully loaded and parsed board data from: " + resourcePath);
      return new BoardAdapter.MapData(dto.size(), snakes, ladders);

    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Failed to read or parse board JSON from resource: " + resourcePath, e);
      throw new JsonParsingException("Failed to read or parse board JSON from resource: " + resourcePath, e);
    }
  }
}