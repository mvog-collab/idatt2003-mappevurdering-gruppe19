package edu.ntnu.idatt2003.controllers;

import java.util.function.Consumer;

import edu.ntnu.idatt2003.game_logic.BoardMaker;
import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.ui.BoardSizePage;
import javafx.stage.Stage;

public class BoardSizeController implements BasePopupController {

    private final BoardSizePage view;
    private final Consumer<Board> boardConsumer;

    public BoardSizeController(BoardSizePage view, Consumer<Board> boardConsumer) {
        this.view = view;
        this.boardConsumer = boardConsumer;
        init();
    }
    
    private void init() {
        view.getSixtyTiles().setOnAction(e -> {
            Board board = BoardMaker.createBoard(60);
            boardConsumer.accept(board);
            System.out.println("Chose 60");
        });
        view.getNinetyTiles().setOnAction(e -> {
            Board board = BoardMaker.createBoard(90);
            boardConsumer.accept(board);
            System.out.println("Chose 90");
        });
        view.getOneTwentyTiles().setOnAction(e -> {
            Board board = BoardMaker.createBoard(120);
            boardConsumer.accept(board);
            System.out.println("Chose 120");
        });
        view.getContinueButton().setOnAction(e -> confirm());
        view.getCancelButton().setOnAction(e -> cancel());
    }

    @Override
    public void confirm() {
        Stage stage = (Stage) view.getContinueButton().getScene().getWindow();
        stage.close();
    }

    
    @Override
    public void cancel() {
        Stage stage = (Stage) view.getCancelButton().getScene().getWindow();
        stage.close();
    }
    
}
