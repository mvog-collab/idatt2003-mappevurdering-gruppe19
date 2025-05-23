package edu.ntnu.idatt2003.model.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing the configuration of a game board,
 * including its size, individual tiles, and any snakes or ladders mappings.
 *
 * @param boardSize the total number of tiles on the board
 * @param tiles a list of {@link TileDTO}s defining each tile's properties
 * @param snakes a map from start tile ID to end tile ID for each snake
 * @param ladders a map from start tile ID to end tile ID for each ladder
 */
public record BoardDTO(
    int boardSize,
    List<TileDTO> tiles,
    Map<Integer, Integer> snakes,
    Map<Integer, Integer> ladders) { }
