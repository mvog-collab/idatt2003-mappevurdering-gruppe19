package edu.ntnu.idatt2003.controllers;

import java.util.function.Consumer;

import edu.ntnu.idatt2003.models.Board;
import edu.ntnu.idatt2003.ui.BoardSizePage;
import edu.ntnu.idatt2003.utils.BoardFactory;
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
        view.getSixtyFourTiles().setOnAction(e -> {
            boardConsumer.accept(BoardFactory.createBoardFromClassPath("/board/board64.json"));
            System.out.println("Chose 64");
        });
        view.getNinetyTiles().setOnAction(e -> {
            boardConsumer.accept(BoardFactory.createBoardFromClassPath("/board/board90.json"));
            System.out.println("Chose 90");
        });
        view.getOneTwentyTiles().setOnAction(e -> {
            boardConsumer.accept(BoardFactory.createBoardFromClassPath("/board/board64.json"));
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
