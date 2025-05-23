package edu.ntnu.idatt2003.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Ludo board configuration loaded from JSON.
 * Contains layout dimensions, tile mapping, and player home positions.
 *
 * @param tileSize      the pixel size of each tile on the board
 * @param tileMap       a 2D list representing the board grid, where each integer
 *                      corresponds to a {@code LudoTile} ID or placeholder
 * @param homePositions a map from player color names (e.g., "RED", "BLUE") to
 *                      a list of coordinate pairs defining their home starting positions
 */
public record LudoBoardConfigDTO(
    @JsonProperty("tileSize") int tileSize,
    @JsonProperty("tileMap") List<List<Integer>> tileMap,
    @JsonProperty("homePositions") Map<String, List<List<Integer>>> homePositions) {
}
