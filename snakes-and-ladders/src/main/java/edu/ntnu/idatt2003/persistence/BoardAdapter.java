package edu.ntnu.idatt2003.persistence;

import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.model.dto.TileDTO;
import java.util.List;
import java.util.Map;

public final class BoardAdapter {

  private BoardAdapter() {}

  public static BoardDTO toDto(
      int boardSize, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {

    List<TileDTO> tiles =
        java.util.stream.IntStream.rangeClosed(1, boardSize)
            .mapToObj(
                id ->
                    new TileDTO(
                        id,
                        snakes.get(id), // null if not a snake
                        ladders.get(id))) // null if not a ladder
            .toList();

    return new BoardDTO(boardSize, tiles, snakes, ladders);
  }

  public static MapData fromDto(BoardDTO dto) {
    return new MapData(dto.boardSize(), dto.snakes(), dto.ladders());
  }

  public record MapData(int boardSize, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {}
}
