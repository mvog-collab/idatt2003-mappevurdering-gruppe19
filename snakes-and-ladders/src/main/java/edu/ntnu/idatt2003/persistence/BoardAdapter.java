package edu.ntnu.idatt2003.persistence;

import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.model.dto.TileDTO;
import java.util.List;
import java.util.Map;

/**
 * Adapter for converting between {@link BoardDTO} and internal map data.
 * <p>
 * Provides methods to create a DTO from board mappings and to reconstruct
 * {@link MapData} from a DTO.
 * </p>
 */
public class BoardAdapter {

  private BoardAdapter() {
    // Prevent instantiation
  }

  /**
   * Builds a {@link BoardDTO} from raw board mappings.
   *
   * @param boardSize the total number of tiles on the board
   * @param snakes    mapping from tile ID to destination ID for snakes
   * @param ladders   mapping from tile ID to destination ID for ladders
   * @return a populated {@link BoardDTO}
   */
  public static BoardDTO toDto(
      int boardSize, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {

    List<TileDTO> tiles = java.util.stream.IntStream.rangeClosed(1, boardSize)
        .mapToObj(id -> new TileDTO(
            id,
            snakes.get(id), // null if no snake
            ladders.get(id))) // null if no ladder
        .toList();

    return new BoardDTO(boardSize, tiles, snakes, ladders);
  }

  /**
   * Converts a {@link BoardDTO} back into internal {@link MapData}.
   *
   * @param dto the board DTO containing size and mappings
   * @return a {@link MapData} instance with the same information
   */
  public static MapData fromDto(BoardDTO dto) {
    return new MapData(dto.boardSize(), dto.snakes(), dto.ladders());
  }

  /**
   * Container for board size and snake/ladder mappings.
   *
   * @param boardSize the total number of tiles
   * @param snakes    mapping from start tile ID to snake end tile ID
   * @param ladders   mapping from start tile ID to ladder end tile ID
   */
  public record MapData(int boardSize,
      Map<Integer, Integer> snakes,
      Map<Integer, Integer> ladders) {
  }
}