package edu.ntnu.idatt2003.utils.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PlayerCsv {

    private PlayerCsv() {}

    public static void save(List<String[]> rows, Path out) throws IOException {
        Files.write(out,
            rows.stream()
                .map(r -> String.join(",", r))
                .toList());
    }

    public static List<String[]> load(Path in) throws IOException {
        return Files.readAllLines(in).stream()
                    .map(l -> l.split(","))
                    .toList();
    }
}