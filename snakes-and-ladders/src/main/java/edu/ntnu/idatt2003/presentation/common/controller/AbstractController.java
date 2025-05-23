package edu.ntnu.idatt2003.presentation.common.controller;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;

public abstract class AbstractController {
  protected final CompleteBoardGame gateway;

  public AbstractController(CompleteBoardGame gateway) {
    this.gateway = gateway;
  }
}
