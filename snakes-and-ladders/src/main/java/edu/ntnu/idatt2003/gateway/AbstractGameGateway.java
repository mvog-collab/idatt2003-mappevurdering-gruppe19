package edu.ntnu.idatt2003.gateway;

import edu.games.engine.exception.GameEngineException;
import edu.games.engine.exception.StorageException;
import edu.games.engine.impl.DefaultGame;
import edu.games.engine.impl.overlay.OverlayProvider;
import edu.games.engine.model.Token;
import edu.games.engine.observer.BoardGameEvent;
import edu.games.engine.observer.BoardGameObserver;
import edu.games.engine.store.PlayerStore;
import edu.ntnu.idatt2003.presentation.fx.OverlayParams;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;

/**
 * Base gateway handling common game lifecycle, persistence, overlay loading, and observer management.
 * Concrete subclasses must implement {@link #mapStringToToken(String)} to convert UI tokens into domain tokens.
 */
public abstract class AbstractGameGateway implements CompleteBoardGame {
  private final List<BoardGameObserver> observers = new ArrayList<>();
  protected final PlayerStore playerStore;
  protected final OverlayProvider overlayProvider;
  protected final Map<Integer, List<OverlayParams>> overlayCache = new HashMap<>();
  protected List<Integer> lastDiceValues = List.of();
  protected DefaultGame game;

  private static final Logger LOG = Logger.getLogger(AbstractGameGateway.class.getName());

  /**
   * Constructs the gateway with required persistence and overlay providers.
   *
   * @param playerStore     store for saving and loading players
   * @param overlayProvider provider for board overlay parameters
   */
  protected AbstractGameGateway(PlayerStore playerStore, OverlayProvider overlayProvider) {
    this.playerStore = playerStore;
    this.overlayProvider = overlayProvider;
    LOG.info(() -> "AbstractGameGateway initialized with PlayerStore: "
        + playerStore.getClass().getSimpleName()
        + " and OverlayProvider: "
        + overlayProvider.getClass().getSimpleName());
  }

  /**
   * Registers an observer to receive game events.
   *
   * @param observer the observer to add
   */
  @Override
  public void addObserver(BoardGameObserver observer) {
    if (observer != null && !observers.contains(observer)) {
      observers.add(observer);
      LOG.fine(() -> "Observer added: " + observer.getClass().getName());
    }
  }

  /**
   * Unregisters a previously added observer.
   *
   * @param observer the observer to remove
   */
  @Override
  public void removeObserver(BoardGameObserver observer) {
    if (observer != null && observers.remove(observer)) {
      LOG.fine(() -> "Observer removed: " + observer.getClass().getName());
    }
  }

  /**
   * Notifies all registered observers of a game event, catching and logging errors per observer.
   *
   * @param event the game event to dispatch
   */
  protected void notifyObservers(BoardGameEvent event) {
    for (BoardGameObserver observer : new ArrayList<>(observers)) {
      try {
        observer.update(event);
      } catch (Exception e) {
        LOG.log(Level.SEVERE,
            "Error in observer " + observer.getClass().getName()
                + " during update for event " + event.getTypeOfEvent(),
            e);
      }
    }
  }

  /**
   * @return {@code true} if the current game has a winner.
   */
  @Override
  public boolean hasWinner() {
    boolean hasWinner = game != null && game.getWinner().isPresent();
    LOG.finest(() -> "Checking for winner: " + hasWinner);
    return hasWinner;
  }

  /**
   * @return the name of the current player, or empty string if unavailable.
   */
  @Override
  public String currentPlayerName() {
    if (game != null && game.currentPlayer() != null) {
      return game.currentPlayer().getName();
    }
    LOG.warning("Attempted to get current player name, but game or current player is null.");
    return "";
  }

  /**
   * @return a copy of the last dice values rolled.
   */
  @Override
  public List<Integer> lastDiceValues() {
    return List.copyOf(lastDiceValues);
  }

  /**
   * Saves the current list of players to the specified file path.
   * Logs and rethrows storage exceptions, logs engine exceptions.
   *
   * @param out the file path to save players to
   */
  @Override
  public void savePlayers(Path out) {
    LOG.info("Attempting to save players to: " + out);
    try {
      if (game != null && game.getPlayers() != null && !game.getPlayers().isEmpty()) {
        playerStore.savePlayers(game.getPlayers(), out);
        LOG.info("Players saved successfully to: " + out);
      } else {
        LOG.warning("No players to save or game not initialized.");
      }
    } catch (StorageException e) {
      LOG.log(Level.SEVERE, "StorageException while saving players to " + out, e);
      throw e;
    } catch (GameEngineException e) {
      LOG.log(Level.WARNING, "GameEngineException during savePlayers: " + e.getMessage(), e);
    }
  }

  /**
   * Clears all players from the current game and notifies observers of a reset.
   */
  @Override
  public void clearPlayers() {
    if (game != null && game.getPlayers() != null) {
      LOG.info("Clearing all players from the game.");
      game.getPlayers().clear();
      notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.GAME_RESET, null));
    } else {
      LOG.warning("Attempted to clear players, but game or players list is null.");
    }
  }

  /**
   * Retrieves the overlay parameters for the current board size, using a cache.
   * If loading fails, logs the error and returns an empty list.
   *
   * @return list of {@link OverlayParams} for the board
   */
  @Override
  public List<OverlayParams> boardOverlays() {
    int size = boardSize();
    if (size <= 0) {
      LOG.warning("Cannot load overlays for invalid board size: " + size);
      return List.of();
    }
    LOG.fine("Fetching board overlays for size: " + size);
    return overlayCache.computeIfAbsent(size, s -> {
      try {
        return loadOverlays(s);
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Failed to load overlays for board size " + s, e);
        return List.of();
      }
    });
  }

  /**
   * Invokes the overlay provider to load overlay parameters for a given size.
   * Wraps and rethrows storage or engine exceptions appropriately.
   *
   * @param size the board size
   * @return list of {@link OverlayParams} loaded
   * @throws StorageException       if loading from provider fails due to I/O or missing resource
   * @throws GameEngineException    if any other failure occurs
   */
  protected List<OverlayParams> loadOverlays(int size) {
    LOG.info("Loading overlays from provider for board size: " + size);
    try {
      List<OverlayParams> loadedOverlays = overlayProvider.overlaysForBoard(size);
      LOG.info("Successfully loaded " + loadedOverlays.size() + " overlays for board size: " + size);
      return loadedOverlays;
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error loading overlays via provider for board size " + size, e);
      if (e instanceof StorageException) {
        throw (StorageException) e;
      }
      throw new GameEngineException("Failed to load overlays for board size " + size, e);
    }
  }

  /**
   * Maps a UI token string to the domain {@link Token} enum.
   *
   * @param token the token string from the UI
   * @return the corresponding {@code Token} enum
   */
  protected abstract Token mapStringToToken(String token);

  /**
   * Loads players from raw CSV rows, parsing name, token, and birthday.
   * Skips or logs malformed rows, then notifies observers once all are loaded.
   *
   * @param rows list of string arrays, each representing [name, token, birthday]
   */
  @Override
  public void loadPlayers(List<String[]> rows) {
    LOG.info("Loading " + (rows != null ? rows.size() : 0) + " players from rows data.");
    if (rows != null) {
      for (String[] r : rows) {
        if (r.length >= 3) {
          try {
            String name = r[0];
            String tokenStr = r[1];
            LocalDate birthday = LocalDate.parse(r[2]);
            addPlayer(name, tokenStr, birthday);
            LOG.fine(() -> "Loaded player: " + name + ", Token: " + tokenStr);
          } catch (DateTimeParseException e) {
            LOG.log(Level.WARNING, "Failed to parse birthday for player data: " + String.join(",", r), e);
          } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Invalid token or other argument for player data: " + String.join(",", r), e);
          } catch (Exception e) {
            LOG.log(Level.WARNING, "Generic error loading player from row: " + String.join(",", r), e);
          }
        } else {
          LOG.warning("Skipping invalid player row (not enough data): " + String.join(",", r));
        }
      }
      notifyObservers(new BoardGameEvent(BoardGameEvent.EventType.PLAYERS_LOADED, players()));
    }
  }
}
