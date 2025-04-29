package edu.ntnu.idatt2003.utils;

import java.util.List;
import java.util.Map;

import edu.ntnu.idatt2003.dto.BoardDTO;

public final class BoardAdapter {

    private BoardAdapter() {}
    
    public static BoardDTO toDto(int boardSize, Map<Integer,Integer> snakes, Map<Integer,Integer> ladders) {
        return new BoardDTO(boardSize, List.of(), snakes, ladders);
    }

    public static MapData fromDto(BoardDTO dto) {
        return new MapData(dto.size(), dto.snakes(), dto.ladders());
    }

    public record MapData(int size,
                        Map<Integer,Integer> snakes,
                        Map<Integer,Integer> ladders) {}
}
