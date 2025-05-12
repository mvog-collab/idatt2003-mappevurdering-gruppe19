package edu.ntnu.idatt2003.gateway;

import edu.games.engine.model.Token;
import java.util.Map;

public final class TokenMapperFactory {
  private static final Map<String, Token> LUDO_TOKENS =
      Map.of(
          "BLUE", Token.BLUE,
          "GREEN", Token.GREEN,
          "YELLOW", Token.YELLOW,
          "RED", Token.RED);

  private static final Map<String, Token> SNL_TOKENS =
      Map.of(
          "BLUE", Token.BLUE,
          "GREEN", Token.GREEN,
          "YELLOW", Token.YELLOW,
          "RED", Token.RED,
          "PURPLE", Token.PURPLE);

  private TokenMapperFactory() {}

  public static Map<String, Token> getLudoTokens() {
    return LUDO_TOKENS;
  }

  public static Map<String, Token> getSnlTokens() {
    return SNL_TOKENS;
  }

  public static Token getLudoToken(String tokenName) {
    return LUDO_TOKENS.get(tokenName);
  }

  public static Token getSnlToken(String tokenName) {
    return SNL_TOKENS.get(tokenName);
  }
}
