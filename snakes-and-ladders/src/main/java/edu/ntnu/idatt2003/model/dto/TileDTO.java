package edu.ntnu.idatt2003.model.dto;

/**
 * Data Transfer Object representing a single board tile in Snakes & Ladders or similar games.
 * Encapsulates the tile's identifier and optional connections to a snake or ladder.
 *
 * @param tileId   unique identifier of the tile
 * @param snakeTo  the destination tile ID if this tile is the head of a snake (null if none)
 * @param ladderTo the destination tile ID if this tile is the bottom of a ladder (null if none)
 */
public record TileDTO(int tileId, Integer snakeTo, Integer ladderTo) {
}
