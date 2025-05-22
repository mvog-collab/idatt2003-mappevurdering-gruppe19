package edu.ntnu.idatt2003.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.idatt2003.exception.JsonParsingException;
import edu.ntnu.idatt2003.model.dto.BoardDTO;
import edu.ntnu.idatt2003.utils.FileHandler;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonBoardHandler implements FileHandler<BoardAdapter.MapData> {

  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private static final Logger LOG = Logger.getLogger(JsonBoardHandler.class.getName());

  @Override
  public void save(BoardAdapter.MapData data, Path out) throws JsonParsingException {
    LOG.info("Saving board data to JSON file: " + out.toString());
    try {
      mapper.writeValue(out.toFile(), BoardAdapter.toDto(data.boardSize(), data.snakes(), data.ladders()));
      LOG.info("Successfully saved board data to: " + out.toString());
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Failed to save board data to JSON file: " + out.toString(), e);
      throw new JsonParsingException("Failed to save board data to JSON file: " + out.toString(), e);
    }
  }

  @Override
  public BoardAdapter.MapData load(Path in) throws JsonParsingException {
    LOG.info("Loading board data from JSON file: " + in.toString());
    try {
      BoardDTO dto = mapper.readValue(in.toFile(), BoardDTO.class);
      LOG.info("Successfully loaded board data from: " + in.toString());
      return BoardAdapter.fromDto(dto);
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Failed to load board data from JSON file: " + in.toString(), e);
      throw new JsonParsingException("Failed to load board data from JSON file: " + in.toString(), e);
    }
  }
}