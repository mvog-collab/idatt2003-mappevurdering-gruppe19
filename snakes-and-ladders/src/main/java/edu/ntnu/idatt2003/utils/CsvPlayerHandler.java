package edu.ntnu.idatt2003.utils;

import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.PlayerTokens;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvPlayerHandler implements FileHandler<List<Player>> {

    private static final String SEP = ",";

    @Override
    public void save(List<Player> players, Path path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            for (Player player : players) {
                writer.write(String.format("%s,%s,%s%n",
                    player.getName(),
                    player.getToken().name(),
                    player.getBirthday()));
            }
        }
    }

    @Override
    public List<Player> load(Path path) throws IOException {
        List<Player> players = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    PlayerTokens token = PlayerTokens.valueOf(parts[1]);
                    LocalDate birthday = LocalDate.parse(parts[2]);
                    players.add(new Player(name, token, birthday));
                }
            }
        }
        return players;
    }
}