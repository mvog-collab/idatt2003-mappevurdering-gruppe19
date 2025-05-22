package edu.ntnu.idatt2003.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.model.dto.TileDTO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class BoardFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private BoardFactory() {}

  public static BoardAdapter.MapData loadFromClasspath(String resourcePath) {

    try (InputStream inputStream = BoardFactory.class.getResourceAsStream(resourcePath)) {

      if (inputStream == null) {
        throw new IllegalArgumentException("Resource not found: " + resourcePath);
      }

      BoardDTO dto = MAPPER.readValue(inputStream, BoardDTO.class);

      Map<Integer, Integer> snakes = new HashMap<>();
      Map<Integer, Integer> ladders = new HashMap<>();

      for (TileDTO tile : dto.tiles()) {
        if (tile.snakeTo() != null) snakes.put(tile.tileId(), tile.snakeTo());
        if (tile.ladderTo() != null) ladders.put(tile.tileId(), tile.ladderTo());
      }

      return new BoardAdapter.MapData(dto.boardSize(), snakes, ladders);

    } catch (IOException e) {
      throw new RuntimeException("Failed to read board JSON", e);
    }
  }
}
