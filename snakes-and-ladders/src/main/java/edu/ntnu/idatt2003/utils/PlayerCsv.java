/* utils/PlayerCsv.java  — very small, no dependencies */
package edu.ntnu.idatt2003.utils;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PlayerCsv {

  private static final String SEP = ",";

  private PlayerCsv() {}

  public static void save(List<String[]> rows, Path out) throws IOException {
    try (BufferedWriter w = new BufferedWriter(new FileWriter(out.toFile()))) {
      for (String[] r : rows) w.write(String.join(SEP, r) + System.lineSeparator());
    }
  }

  /** rows are returned as String[]{name, token, birthday} */
  public static List<String[]> load(Path in) throws IOException {
    List<String[]> rows = new ArrayList<>();
    try (BufferedReader r = new BufferedReader(new FileReader(in.toFile()))) {
      String line;
      while ((line = r.readLine()) != null) rows.add(line.split(SEP));
    }
    return rows;
  }
}