package edu.ntnu.idatt2003.ui.service.animation;

import java.util.List;

public interface AnimationService {
    void animateMove(String tokenName, int startId, int endId, Runnable onFinished);
    void animateMoveAlongPath(String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished);
}