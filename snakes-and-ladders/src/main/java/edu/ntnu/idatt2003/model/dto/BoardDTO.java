package edu.ntnu.idatt2003.model.dto;

import java.util.List;
import java.util.Map;

public record BoardDTO(int size, List<TileDTO> tiles, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
}