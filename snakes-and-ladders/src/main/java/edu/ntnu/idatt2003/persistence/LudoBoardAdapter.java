package edu.ntnu.idatt2003.persistence;

import edu.ntnu.idatt2003.model.dto.LudoBoardConfigDTO;
import javafx.geometry.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter for converting a {@link LudoBoardConfigDTO} into runtime map data.
 * <p>
 * Transforms the DTO’s tile size, tile layout, and home position definitions
 * into pixel coordinates for rendering on a JavaFX board.
 * </p>
 */
public class LudoBoardAdapter {

    /**
     * Container for Ludo board rendering data.
     *
     * @param tileSize      the size of each board tile in pixels
     * @param coordinates   mapping from tile ID to its center {@link Point2D}
     *                      coordinate
     * @param homePositions mapping from player color (uppercase) to list of home
     *                      slot coordinates
     */
    public record LudoMapData(
            int tileSize,
            Map<Integer, Point2D> coordinates,
            Map<String, List<Point2D>> homePositions) {
    }

    /**
     * Builds a {@link LudoMapData} instance from the provided configuration DTO.
     *
     * @param dto the board configuration DTO containing tile size and layout
     * @return a fully populated {@link LudoMapData}
     */
    public static LudoMapData fromDto(LudoBoardConfigDTO dto) {
        int tileSize = dto.tileSize();
        Map<Integer, Point2D> coordinates = buildCoordinatesFromDto(dto.tileMap(), tileSize);
        Map<String, List<Point2D>> homePositions = buildHomePositionsFromDto(dto.homePositions(), tileSize);
        return new LudoMapData(tileSize, coordinates, homePositions);
    }

    /**
     * Calculates the center point of a tile based on its row and column indices.
     *
     * @param row      the tile’s row index in the grid
     * @param col      the tile’s column index in the grid
     * @param tileSize the size of each tile in pixels
     * @return a {@link Point2D} representing the tile’s center coordinate
     */
    private static Point2D createPoint(int row, int col, int tileSize) {
        double x = col * tileSize + tileSize / 2.0;
        double y = row * tileSize + tileSize / 2.0;
        return new Point2D(x, y);
    }

    /**
     * Constructs a mapping from tile IDs to their pixel coordinates.
     *
     * @param tileMapDto the 2D list of tile IDs (null or non-positive IDs are
     *                   skipped)
     * @param tileSize   the size of each tile in pixels
     * @return a map from valid tile ID to its center {@link Point2D}
     */
    private static Map<Integer, Point2D> buildCoordinatesFromDto(
            List<List<Integer>> tileMapDto, int tileSize) {
        Map<Integer, Point2D> map = new HashMap<>();
        if (tileMapDto == null) {
            return map;
        }
        for (int row = 0; row < tileMapDto.size(); row++) {
            List<Integer> rowData = tileMapDto.get(row);
            if (rowData == null) {
                continue;
            }
            for (int col = 0; col < rowData.size(); col++) {
                Integer id = rowData.get(col);
                if (id != null && id > 0) {
                    map.put(id, createPoint(row, col, tileSize));
                }
            }
        }
        return map;
    }

    /**
     * Builds home slot positions for each player color from the DTO.
     *
     * @param homePositionsDto mapping from color name to list of [row, col] pairs
     * @param tileSize         the tile size in pixels
     * @return a map from uppercase color name to list of {@link Point2D} home slot
     *         centers
     */
    private static Map<String, List<Point2D>> buildHomePositionsFromDto(
            Map<String, List<List<Integer>>> homePositionsDto, int tileSize) {
        Map<String, List<Point2D>> positions = new HashMap<>();
        if (homePositionsDto == null) {
            return positions;
        }
        homePositionsDto.forEach((color, coordsList) -> {
            if (coordsList == null) {
                return;
            }
            List<Point2D> points = coordsList.stream()
                    .filter(coordPair -> coordPair != null && coordPair.size() == 2)
                    .map(coordPair -> createPoint(coordPair.get(0), coordPair.get(1), tileSize))
                    .collect(Collectors.toList());
            positions.put(color.toUpperCase(), points);
        });
        return positions;
    }
}