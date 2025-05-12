package edu.ntnu.idatt2003.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.utils.FileHandler;
import java.io.IOException;
import java.nio.file.Path;

public class JsonBoardHandler implements FileHandler<BoardAdapter.MapData> {

  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  @Override
  public void save(BoardAdapter.MapData data, Path out) throws IOException {
    mapper.writeValue(out.toFile(), BoardAdapter.toDto(data.size(), data.snakes(), data.ladders()));
  }

  @Override
  public BoardAdapter.MapData load(Path in) throws IOException {
    BoardDTO dto = mapper.readValue(in.toFile(), BoardDTO.class);
    return BoardAdapter.fromDto(dto);
  }
}
