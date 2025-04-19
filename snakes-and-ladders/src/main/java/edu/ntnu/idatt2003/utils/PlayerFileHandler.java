package edu.ntnu.idatt2003.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idatt2003.models.Player;
import edu.ntnu.idatt2003.models.PlayerTokens;

public class PlayerFileHandler {

    private static final String DELIMITER = ",";

    public static void savePlayer(List<Player> players, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                writer.write(player.getName() + DELIMITER + player.getToken());
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            //TODO: add error handling/possible GUI update (in parameter maybe?)
        }
    }

    public static List<Player> loadPlayers(String filePath) {
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) == null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String token = parts[1].trim();
                    //TODO: find out how we want to implement birthday
                    //Player player = new Player(name, token, LocalDate.now());
                    //players.add(player);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            //TODO: add error handling/possible GUI update (in parameter maybe?)
        }
        return players;
    }
}
