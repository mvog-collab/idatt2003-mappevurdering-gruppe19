package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;

/**
 * Base controller providing access to the shared game gateway.
 * <p>
 * All specific controllers should extend this class to gain access
 * to the {@link CompleteBoardGame} instance for game operations.
 * </p>
 */
public abstract class AbstractController {

  /** The game gateway for invoking game operations and registering observers. */
  protected final CompleteBoardGame gateway;

  /**
   * Constructs a controller with the given game gateway.
   *
   * @param gateway the shared {@link CompleteBoardGame} instance
   */
  public AbstractController(CompleteBoardGame gateway) {
    this.gateway = gateway;
  }
}