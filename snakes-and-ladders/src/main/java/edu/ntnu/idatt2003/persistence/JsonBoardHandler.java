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

/**
 * JSON-based implementation of {@link FileHandler} for board map data.
 * <p>
 * Serializes and deserializes {@link BoardAdapter.MapData} to and from
 * a JSON file with pretty-print formatting.
 * </p>
 */
public class JsonBoardHandler implements FileHandler<BoardAdapter.MapData> {

  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private static final Logger LOG = Logger.getLogger(JsonBoardHandler.class.getName());

  /**
   * Saves the given board map data as JSON to the specified path.
   *
   * @param data the map data to serialize
   * @param out  the output file path
   * @throws JsonParsingException if an I/O error occurs during serialization
   */
  @Override
  public void save(BoardAdapter.MapData data, Path out) throws JsonParsingException {
    LOG.info("Saving board data to JSON file: " + out);
    try {
      mapper.writeValue(out.toFile(),
          BoardAdapter.toDto(data.boardSize(), data.snakes(), data.ladders()));
      LOG.info("Successfully saved board data to: " + out);
    } catch (IOException e) {
      LOG.log(Level.SEVERE,
          "Failed to save board data to JSON file: " + out, e);
      throw new JsonParsingException(
          "Failed to save board data to JSON file: " + out, e);
    }
  }

  /**
   * Loads board map data from the specified JSON file.
   *
   * @param in the input file path
   * @return the deserialized {@link BoardAdapter.MapData}
   * @throws JsonParsingException if an I/O error occurs during deserialization
   */
  @Override
  public BoardAdapter.MapData load(Path in) throws JsonParsingException {
    LOG.info("Loading board data from JSON file: " + in);
    try {
      BoardDTO dto = mapper.readValue(in.toFile(), BoardDTO.class);
      LOG.info("Successfully loaded board data from: " + in);
      return BoardAdapter.fromDto(dto);
    } catch (IOException e) {
      LOG.log(Level.SEVERE,
          "Failed to load board data from JSON file: " + in, e);
      throw new JsonParsingException(
          "Failed to load board data from JSON file: " + in, e);
    }
  }
}