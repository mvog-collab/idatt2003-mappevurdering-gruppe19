package edu.ntnu.idatt2003.presentation.service.animation;

import java.util.List;

/**
 * Interface for game piece animation services.
 * <p>
 * Handles smooth movement animations between board positions
 * with completion callbacks for game flow coordination.
 * </p>
 */
public interface AnimationService {
  /**
   * Animates token movement between two positions.
   *
   * @param tokenName  the identifier of the token to animate
   * @param startId    the starting position
   * @param endId      the ending position
   * @param onFinished callback to execute when animation completes
   */
  void animateMove(String tokenName, int startId, int endId, Runnable onFinished);

  /**
   * Animates token movement along a specified path.
   *
   * @param tokenName  the identifier of the token to animate
   * @param pieceIndex the index of the piece for multi-piece games
   * @param path       the sequence of positions to move through
   * @param onFinished callback to execute when animation completes
   */
  void animateMoveAlongPath(
      String tokenName, int pieceIndex, List<Integer> path, Runnable onFinished);
}