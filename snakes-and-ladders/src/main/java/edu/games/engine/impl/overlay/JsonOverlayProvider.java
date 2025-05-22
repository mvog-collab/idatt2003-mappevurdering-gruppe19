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

public final class JsonOverlayProvider implements OverlayProvider {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final String baseDir;
  private static final Logger LOG = Logger.getLogger(JsonOverlayProvider.class.getName());

  public JsonOverlayProvider(String baseResourceDir) {
    this.baseDir = baseResourceDir.endsWith("/") ? baseResourceDir : baseResourceDir + "/";
    LOG.info("JsonOverlayProvider initialized with base directory: " + this.baseDir);
  }

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