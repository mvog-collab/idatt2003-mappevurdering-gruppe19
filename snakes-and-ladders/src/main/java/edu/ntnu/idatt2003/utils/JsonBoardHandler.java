package edu.ntnu.idatt2003.utils;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ntnu.idatt2003.dto.BoardDTO;
import edu.ntnu.idatt2003.models.Board;

public class JsonBoardHandler implements FileHandler<Board>{

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void save(Board board, Path path) throws IOException {
        BoardDTO dto = BoardAdapter.toDto(board);
        mapper.writeValue(path.toFile(), dto);
    }

    @Override
    public Board load(Path path) throws IOException {
        BoardDTO dto = mapper.readValue(path.toFile(), BoardDTO.class);
        return BoardAdapter.fromDto(dto);
    }
}
