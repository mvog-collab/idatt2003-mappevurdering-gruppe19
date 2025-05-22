package edu.ntnu.idatt2003.persistence;

import edu.ntnu.idatt2003.model.dto.LudoBoardConfigDTO;
import javafx.geometry.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LudoBoardAdapter {

    public record LudoMapData(
            int tileSize,
            Map<Integer, Point2D> coordinates,
            Map<String, List<Point2D>> homePositions) {
    }

    public static LudoMapData fromDto(LudoBoardConfigDTO dto) {
        int tileSize = dto.tileSize();
        Map<Integer, Point2D> coordinates = buildCoordinatesFromDto(dto.tileMap(), tileSize);
        Map<String, List<Point2D>> homePositions = buildHomePositionsFromDto(dto.homePositions(), tileSize);
        return new LudoMapData(tileSize, coordinates, homePositions);
    }

    private static Point2D createPoint(int row, int col, int tileSize) {
        double x = col * tileSize + tileSize / 2.0;
        double y = row * tileSize + tileSize / 2.0;
        return new Point2D(x, y);
    }

    private static Map<Integer, Point2D> buildCoordinatesFromDto(List<List<Integer>> tileMapDto, int tileSize) {
        Map<Integer, Point2D> map = new HashMap<>();
        if (tileMapDto == null)
            return map;
        for (int row = 0; row < tileMapDto.size(); row++) {
            List<Integer> rowData = tileMapDto.get(row);
            if (rowData == null)
                continue;
            for (int col = 0; col < rowData.size(); col++) {
                Integer id = rowData.get(col);
                if (id != null && id > 0) {
                    map.put(id, createPoint(row, col, tileSize));
                }
            }
        }
        return map;
    }

    private static Map<String, List<Point2D>> buildHomePositionsFromDto(
            Map<String, List<List<Integer>>> homePositionsDto, int tileSize) {
        Map<String, List<Point2D>> positions = new HashMap<>();
        if (homePositionsDto == null)
            return positions;
        homePositionsDto.forEach((color, coordsList) -> {
            if (coordsList == null)
                return;
            List<Point2D> points = coordsList.stream()
                    .filter(coordPair -> coordPair != null && coordPair.size() == 2)
                    .map(coordPair -> createPoint(coordPair.get(0), coordPair.get(1), tileSize))
                    .collect(Collectors.toList());
            positions.put(color.toUpperCase(), points);
        });
        return positions;
    }
}