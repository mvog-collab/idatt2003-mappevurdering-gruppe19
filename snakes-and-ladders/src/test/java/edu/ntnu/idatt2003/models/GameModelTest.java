package edu.ntnu.idatt2003.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GameModelTest {

    private Board board;
    private Dice dice;
    private GameModel gameModel;

    // This setUp is somewhat written by ChatGBT
    @BeforeEach
    void setUp() {
        // Create a board with a chain of tiles (IDs 0 to 9)
        board = new Board(10);
        for (int i = 0; i < 10; i++) {
            board.addTile(new Tile(i));
        }
        // Create a custom Dice that returns a fixed value (3) for predictability.
        dice = new Dice() {
            @Override
            public int rollDice() {
                // Set fixed values so that the sum is 3 (e.g. 1 and 2).
                this.getDiceList().get(0).setLastRolledValue(1);
                this.getDiceList().get(1).setLastRolledValue(2);
                return 3;
            }
        };
        gameModel = new GameModel(board, dice);
    }

    @Nested
    @DisplayName("addPlayer Tests")
    class AddPlayerTests {

        @Test
        @DisplayName("addPlayer should add a new player and set the start position")
        void testAddPlayer() {
            gameModel.addPlayer("TestUser", "Token", LocalDate.of(2000, 1, 1));
            assertEquals(1, gameModel.getPlayers().size());
            // The player should be placed on tile 0.
            Player player = gameModel.getPlayers().get(0);
            assertEquals(0, player.getCurrentTile().getTileId());
        }
    }

    @Nested
    @DisplayName("moveCurrentPlayer Tests")
    class MoveCurrentPlayerTests {

        @Test
        @DisplayName("moveCurrentPlayer should move the current player based on the dice roll")
        void testMoveCurrentPlayer() {
            gameModel.addPlayer("Martha", "Token", LocalDate.of(2004, 1, 19));
            // With a fixed dice roll of 3, the player should move from tile 0 to tile 3.
            Optional<Tile> newTile = gameModel.moveCurrentPlayer(gameModel.getDice().rollDice());
            assertTrue(newTile.isPresent());
            assertEquals(3, newTile.get().getTileId());
        }
    }

    @Nested
    @DisplayName("nextPlayersTurn Tests")
    class NextPlayersTurnTests {

        @Test
        @DisplayName("nextPlayersTurn should cycle through the players")
        void testNextPlayersTurn() {
            gameModel.addPlayer("Martha", "Token", LocalDate.of(2002, 1, 19));
            gameModel.addPlayer("Edvard", "Token", LocalDate.of(2003, 3, 27));
            gameModel.getDice().getDiceList().getFirst().setLastRolledValue(1);
            gameModel.getDice().getDiceList().getLast().setLastRolledValue(2);
            Player currentPlayer = gameModel.getCurrentPlayer();
            Player nextPlayer = gameModel.nextPlayersTurn();
            assertNotEquals(currentPlayer, nextPlayer);
        }
    }

    @Nested
    @DisplayName("setStartPosition Tests")
    class SetStartPositionTests {

        @Test
        @DisplayName("setStartPosition should throw exception if the start tile (tile 0) does not exist")
        void testSetStartPositionNoStartTile() {
            // Create a new board without tile 0.
            Board newBoard = new Board(5);
            for (int i = 1; i < 5; i++) {
                newBoard.addTile(new Tile(i));
            }
            GameModel gm = new GameModel(newBoard, dice);
            assertThrows(IllegalStateException.class, () -> gm.addPlayer("TestUser", "Token", LocalDate.of(2000, 1, 1)));
        }
    }

    @Nested
    @DisplayName("hasPlayerWon Tests")
    class HasPlayerWonTests {

        @Test
        @DisplayName("hasPlayerWon should return true if the player's current tile has no next tile")
        void testHasPlayerWonTrue() {
            gameModel.addPlayer("Martha", "Token", LocalDate.of(2004, 1, 27));
            Player player = gameModel.getPlayers().get(0);
            // Manually place the player on the last tile (tile 9, which has no next tile).
            player.setCurrentTile(board.getTile(9));
            assertTrue(gameModel.hasPlayerWon(player));
        }

        @Test
        @DisplayName("hasPlayerWon should return false if the player's current tile has a next tile")
        void testHasPlayerWonFalse() {
            gameModel.addPlayer("Edvard", "Token", LocalDate.of(2004, 3, 27));
            Player player = gameModel.getPlayers().get(0);
            // Starting at tile 0, which has a next tile.
            assertFalse(gameModel.hasPlayerWon(player));
        }
    }

    @Nested
    @DisplayName("setCurrentPlayer Tests")
    class SetCurrentPlayerTests {

        @Test
        @DisplayName("setCurrentPlayer should throw exception when setting a null or the same player")
        void testSetCurrentPlayerInvalid() {
            gameModel.addPlayer("TestUser", "Token", LocalDate.of(2000, 1, 1));
            Player current = gameModel.getCurrentPlayer();
            assertThrows(IllegalArgumentException.class, () -> gameModel.setCurrentPlayer(null));
            assertThrows(IllegalArgumentException.class, () -> gameModel.setCurrentPlayer(current));
        }
    }
}