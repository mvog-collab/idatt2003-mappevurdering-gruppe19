package edu.ntnu.idatt2003.dto;

import java.util.List;

public record BoardDTO(int size, List<TileDTO> tiles) {
}