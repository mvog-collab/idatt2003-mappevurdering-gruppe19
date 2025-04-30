package edu.games.engine.impl.overlay;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ntnu.idatt2003.ui.fx.OverlayParams;

public final class JsonOverlayProvider implements OverlayProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String baseDir;                    // e.g. "/overlays/"

    public JsonOverlayProvider(String baseResourceDir) {
        this.baseDir = baseResourceDir.endsWith("/") ? baseResourceDir : baseResourceDir + "/";
    }

    @Override
    public List<OverlayParams> overlaysForBoard(int size) {
        String resource = baseDir + "overlays" + size + ".json";

        try (InputStream in = getClass().getResourceAsStream(resource)) {
            if (in == null) return List.of();        // no overlays available

            JsonNode root = MAPPER.readTree(in);
            
            JsonNode entries = root.has("overlays") ? root.get("overlays") : root;
            List<OverlayParams> list = new ArrayList<>();
            for (JsonNode n : entries) {
                list.add(new OverlayParams(
                        n.get("imagePath").asText(),
                        n.get("offsetX").asDouble(),
                        n.get("offsetY").asDouble(),
                        n.get("fitWidth").asDouble(),
                        n.get("startTileId").asInt()));
            }
            return List.copyOf(list);

        } catch (Exception ex) {
            ex.printStackTrace();                    // log & continue without overlays
            return List.of();
        }
    }
}