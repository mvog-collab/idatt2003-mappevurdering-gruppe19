package edu.ntnu.idatt2003.gateway;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import edu.ntnu.idatt2003.ui.OverlayParams;
import edu.ntnu.idatt2003.utils.BoardAdapter;

public interface GameGateway {

  void newGame(int boardSize);
  void newGame(BoardAdapter.MapData data);
  void addPlayer(String name, String token, LocalDate birthday);
  void loadPlayers(List<String[]> rows);
  void savePlayers(java.nio.file.Path out) throws java.io.IOException;

  int  rollDice();
  boolean hasWinner();
  String currentPlayerName();

  /*  READ-ONLY SNAPSHOTS for UI  */
  int                boardSize();
  List<OverlayParams> boardOverlays();
  List<PlayerView>    players();            // name, token, tileId, isTurn
}