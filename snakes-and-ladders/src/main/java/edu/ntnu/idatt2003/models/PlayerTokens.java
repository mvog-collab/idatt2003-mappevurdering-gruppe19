package edu.ntnu.idatt2003.models;

import edu.ntnu.idatt2003.utils.ResourcePaths;

public enum PlayerTokens {
    BLACK("QueenChessBlack.png"),
    GOLD("QueenChessGold.png"),
    SILVER("QueenChessSilver.png"),
    WHITE("QueenChessWhite.png"),
    WOOD("QueenChessWood.png");

    private final String imageFile;

    PlayerTokens(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImagePath() {
        return ResourcePaths.IMAGE_DIR + imageFile;
    }
}
