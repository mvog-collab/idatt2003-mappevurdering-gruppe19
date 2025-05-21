package edu.games.engine.exception;

public class RuleViolationException extends GameEngineException {

  public RuleViolationException(String message) {
    super(message);
  }

  public RuleViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}


