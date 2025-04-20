package edu.ntnu.idatt2003.models;

import edu.ntnu.idatt2003.utils.ResourcePaths;

public enum PlayerTokens {
    BLUE("bluePiece.png"),
    GREEN("greenPiece.png"),
    YELLOW("yellowPiece.png"),
    RED("redPiece.png"),
    PURPLE("purplePiece.png");

    private final String imageFile;

    PlayerTokens(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImagePath() {
        return ResourcePaths.IMAGE_DIR + imageFile;
    }
}
