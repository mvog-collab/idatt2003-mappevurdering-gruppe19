package edu.ntnu.idatt2003.gateway;

import edu.games.engine.model.Token;
import java.util.Map;

/**
 * Factory for mapping between string representations of player tokens
 * and the corresponding {@link Token} enum values for different games.
 *
 * <p>Provides lookup tables for both Ludo and Snakes &amp; Ladders tokens.
 */
public final class TokenMapperFactory {

  /**
   * Token mappings supported in Ludo.
   */
  private static final Map<String, Token> LUDO_TOKENS = Map.of(
      "BLUE", Token.BLUE,
      "GREEN", Token.GREEN,
      "YELLOW", Token.YELLOW,
      "RED", Token.RED
  );

  /**
   * Token mappings supported in Snakes &amp; Ladders.
   */
  private static final Map<String, Token> SNL_TOKENS = Map.of(
      "BLUE", Token.BLUE,
      "GREEN", Token.GREEN,
      "YELLOW", Token.YELLOW,
      "RED", Token.RED,
      "PURPLE", Token.PURPLE
  );

  // Prevent instantiation
  private TokenMapperFactory() { }

  /**
   * Returns an unmodifiable map of valid token names to {@link Token} values for Ludo.
   *
   * @return map of uppercase token names to {@link Token} enums for Ludo
   */
  public static Map<String, Token> getLudoTokens() {
    return LUDO_TOKENS;
  }

  /**
   * Returns an unmodifiable map of valid token names to {@link Token} values for Snakes &amp; Ladders.
   *
   * @return map of uppercase token names to {@link Token} enums for Snakes &amp; Ladders
   */
  public static Map<String, Token> getSnlTokens() {
    return SNL_TOKENS;
  }

  /**
   * Looks up a {@link Token} by its name for Ludo.
   *
   * @param tokenName the uppercase name of the token (e.g. "BLUE")
   * @return the corresponding {@link Token}, or {@code null} if not found
   */
  public static Token getLudoToken(String tokenName) {
    return LUDO_TOKENS.get(tokenName);
  }

  /**
   * Looks up a {@link Token} by its name for Snakes &amp; Ladders.
   *
   * @param tokenName the uppercase name of the token (e.g. "PURPLE")
   * @return the corresponding {@link Token}, or {@code null} if not found
   */
  public static Token getSnlToken(String tokenName) {
    return SNL_TOKENS.get(tokenName);
  }
}
