package edu.ntnu.idatt2003.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import edu.ntnu.idatt2003.models.Board;

public final class BoardFactory {

    private static final JsonBoardHandler handler = new JsonBoardHandler();

    private BoardFactory() { }

    public static Board createBoardFromClassPath(String resourcePath) {
        try (InputStream inputStream = BoardFactory.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Could not find: " + resourcePath);
            }
            Path tmp = Files.createTempFile("board", ".json");
            Files.copy(inputStream, tmp, StandardCopyOption.REPLACE_EXISTING);
            return handler.load(tmp);
            } catch (IOException e) {
            throw new RuntimeException("Could not read file: ", e);
        }
    }
}
