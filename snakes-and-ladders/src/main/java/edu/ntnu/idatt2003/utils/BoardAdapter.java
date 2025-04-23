package edu.ntnu.idatt2003.utils;

import java.util.List;

import edu.ntnu.idatt2003.dto.BoardDTO;
import edu.ntnu.idatt2003.dto.TileDTO;
import edu.ntnu.idatt2003.game_logic.Ladder;
import edu.ntnu.idatt2003.game_logic.Snake;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.models.Tile;

public final class BoardAdapter {

    private BoardAdapter() {}
    
    public static BoardDTO toDto(Board board) {
        List<TileDTO> tiles = board.getTiles().values().stream()
            .map(t -> {
                Integer snake = null;
                Integer ladder = null;
                if (t.getAction() instanceof Snake s) snake = s.getEndTileId();
                if (t.getAction() instanceof Ladder l) ladder = l.getEndTileId();
                return new TileDTO(t.getTileId(), snake, ladder);
            })
            .toList();
            return new BoardDTO(board.getSize(), tiles);
    }

    public static Board fromDto(BoardDTO dto) {
        Board board = new Board(dto.size());
        dto.tiles().forEach(tileDto -> {
            Tile tile = new Tile(tileDto.id());
            board.addTile(tile);
        });
        dto.tiles().forEach(tileDto -> {
            if (tileDto.snakeTo() != null) 
              board.getTile(tileDto.id()).setAction(
                new Snake(tileDto.id(), tileDto.snakeTo(), board));
            if (tileDto.ladderTo() != null)
              board.getTile(tileDto.id()).setAction(
                new Ladder(tileDto.id(), tileDto.ladderTo(), board));
        });
        return board;
    }
}
