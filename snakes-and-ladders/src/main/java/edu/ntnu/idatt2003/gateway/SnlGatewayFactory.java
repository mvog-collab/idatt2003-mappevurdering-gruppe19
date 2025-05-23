package edu.ntnu.idatt2003.gateway;

import edu.games.engine.board.factory.JsonBoardLoader;
import edu.games.engine.board.factory.LinearBoardFactory;
import edu.games.engine.dice.factory.DiceFactory;
import edu.games.engine.dice.factory.RandomDiceFactory;
import edu.games.engine.impl.CsvPlayerStore;
import edu.games.engine.impl.overlay.JsonOverlayProvider;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.store.PlayerStore;

/**
 * Factory for creating a default {@link SnlGateway} instance with
 * standard dependencies wired together.
 * <p>
 * Uses:
 * <ul>
 *   <li>{@link LinearBoardFactory} for board loading</li>
 *   <li>{@link RandomDiceFactory} for dice creation</li>
 *   <li>{@link CsvPlayerStore} for player persistence</li>
 *   <li>{@link JsonOverlayProvider} for UI overlays</li>
 * </ul>
 */
public final class SnlGatewayFactory {

  /**
   * Creates and returns a fully configured {@link SnlGateway} using
   * the default JSON-based board loader, random dice, CSV player store,
   * and JSON overlay provider.
   *
   * @return a ready-to-use {@link SnlGateway} instance
   */
  public static SnlGateway createDefault() {
    JsonBoardLoader boardLoader = new LinearBoardFactory();
    DiceFactory diceFactory = new RandomDiceFactory();
    PlayerStore playerStore = new CsvPlayerStore();
    OverlayProvider overlayProv = new JsonOverlayProvider("/overlays/");

    return new SnlGateway(boardLoader, diceFactory, playerStore, overlayProv);
  }

  /**
   * Private constructor to prevent instantiation.
   */
  private SnlGatewayFactory() {
    // no-op
  }
}
