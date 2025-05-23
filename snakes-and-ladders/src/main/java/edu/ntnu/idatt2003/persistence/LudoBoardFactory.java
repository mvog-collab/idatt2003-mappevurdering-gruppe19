package edu.ntnu.idatt2003.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2003.exception.JsonParsingException;
import edu.ntnu.idatt2003.exception.ResourceNotFoundException;
import edu.ntnu.idatt2003.model.dto.LudoBoardConfigDTO;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for loading Ludo board configuration from JSON resources.
 * <p>
 * Reads a {@link LudoBoardConfigDTO} from the classpath and converts it
 * into {@link LudoBoardAdapter.LudoMapData}.
 * </p>
 */
public class LudoBoardFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = Logger.getLogger(LudoBoardFactory.class.getName());

    private LudoBoardFactory() {
        // Prevent instantiation
    }

    /**
     * Loads Ludo board map data from a JSON resource on the classpath.
     *
     * @param resourcePath the classpath location of the Ludo board JSON
     * @return a {@link LudoBoardAdapter.LudoMapData} ready for rendering
     * @throws ResourceNotFoundException if the resource is missing
     * @throws JsonParsingException      if reading or parsing fails
     */
    public static LudoBoardAdapter.LudoMapData loadFromClasspath(String resourcePath) {
        LOG.info("Loading Ludo board data from classpath resource: " + resourcePath);
        try (InputStream is = LudoBoardFactory.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOG.log(Level.SEVERE, "Ludo board resource not found: " + resourcePath);
                throw new ResourceNotFoundException("Ludo board resource not found: " + resourcePath);
            }
            LudoBoardConfigDTO dto = MAPPER.readValue(is, LudoBoardConfigDTO.class);
            LudoBoardAdapter.LudoMapData mapData = LudoBoardAdapter.fromDto(dto);
            LOG.info("Successfully loaded and parsed Ludo board data from: " + resourcePath);
            return mapData;
        } catch (IOException e) {
            LOG.log(Level.SEVERE,
                    "Failed to read or parse Ludo board JSON from resource: " + resourcePath,
                    e);
            throw new JsonParsingException(
                    "Failed to read or parse Ludo board JSON: " + resourcePath, e);
        }
    }
}