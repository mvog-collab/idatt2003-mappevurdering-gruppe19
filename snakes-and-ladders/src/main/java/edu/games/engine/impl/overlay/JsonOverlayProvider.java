package edu.games.engine.impl.overlay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.games.engine.exception.StorageException;
import edu.ntnu.idatt2003.ui.fx.OverlayParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads graphical overlay data for game boards from JSON resources.
 * Each overlay describes an image placed on a specific tile.
 */
public class JsonOverlayProvider implements OverlayProvider {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final String baseDir;
  private static final Logger LOG = Logger.getLogger(JsonOverlayProvider.class.getName());

  /**
   * Creates a new overlay provider that looks for overlay files in a given
   * resource folder.
   *
   * @param baseResourceDir the base directory in the classpath where overlay
   *                        files are located
   */
  public JsonOverlayProvider(String baseResourceDir) {
    this.baseDir = baseResourceDir.endsWith("/") ? baseResourceDir : baseResourceDir + "/";
    LOG.info("JsonOverlayProvider initialized with base directory: " + this.baseDir);
  }

  /**
   * Loads overlay parameters for a specific board size.
   *
   * @param size the size of the board (used to determine which file to load)
   * @return a list of overlay parameters, or an empty list if no file is found
   * @throws StorageException if the file can't be read or parsed
   */
  @Override
  public List<OverlayParams> overlaysForBoard(int size) throws StorageException {
    String resource = baseDir + "overlays" + size + ".json";
    LOG.info("Attempting to load overlays from resource: " + resource);
    try (InputStream in = getClass().getResourceAsStream(resource)) {
      if (in == null) {
        LOG.warning("Overlay resource not found: " + resource + ". Returning empty list.");
        return List.of();
      }

      JsonNode root = MAPPER.readTree(in);
      JsonNode entries = root.has("overlays") ? root.get("overlays") : root;

      List<OverlayParams> list = new ArrayList<>();
      if (entries.isArray()) {
        for (JsonNode n : entries) {
          try {
            list.add(
                new OverlayParams(
                    n.get("imagePath").asText(),
                    n.get("offsetX").asDouble(),
                    n.get("offsetY").asDouble(),
                    n.get("fitWidth").asDouble(),
                    n.get("startTileId").asInt()));
          } catch (NullPointerException e) {
            LOG.log(Level.WARNING,
                "Skipping overlay entry due to missing field(s) in " + resource + ": " + n.toString(), e);
          }
        }
      } else {
        LOG.warning("Expected 'overlays' to be an array in " + resource + ", but it was not. Node type: "
            + entries.getNodeType());
      }
      LOG.info("Successfully loaded " + list.size() + " overlays from " + resource);
      return List.copyOf(list);

    } catch (JsonProcessingException e) {
      LOG.log(Level.SEVERE, "Failed to parse JSON for overlay file " + resource, e);
      throw new StorageException("Failed to parse JSON for overlay file " + resource, e);
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, "IOException while reading overlay file " + resource, ex);
      throw new StorageException("Failed to read overlay file " + resource, ex);
    }
  }
}
