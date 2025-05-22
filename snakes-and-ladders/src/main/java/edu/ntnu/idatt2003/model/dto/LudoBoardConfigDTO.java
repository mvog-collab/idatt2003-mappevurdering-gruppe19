package edu.ntnu.idatt2003.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record LudoBoardConfigDTO(
        @JsonProperty("tileSize") int tileSize,
        @JsonProperty("tileMap") List<List<Integer>> tileMap,
        @JsonProperty("homePositions") Map<String, List<List<Integer>>> homePositions) {
}