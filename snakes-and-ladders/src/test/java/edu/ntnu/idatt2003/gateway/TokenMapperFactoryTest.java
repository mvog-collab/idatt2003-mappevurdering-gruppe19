package edu.ntnu.idatt2003.gateway;

import edu.games.engine.model.Token;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TokenMapperFactoryTest {

    @Nested
    class LudoTokens {

        @Test
        void shouldReturnLudoTokensMap() {
            Map<String, Token> tokens = TokenMapperFactory.getLudoTokens();

            assertNotNull(tokens);
            assertEquals(4, tokens.size());
            assertEquals(Token.BLUE, tokens.get("BLUE"));
            assertEquals(Token.GREEN, tokens.get("GREEN"));
            assertEquals(Token.YELLOW, tokens.get("YELLOW"));
            assertEquals(Token.RED, tokens.get("RED"));
        }

        @Test
        void shouldReturnImmutableLudoTokensMap() {
            Map<String, Token> tokens = TokenMapperFactory.getLudoTokens();

            assertThrows(UnsupportedOperationException.class, () -> tokens.put("PURPLE", Token.PURPLE));
        }

        @Test
        void shouldReturnSameLudoTokensMapOnMultipleCalls() {
            Map<String, Token> tokens1 = TokenMapperFactory.getLudoTokens();
            Map<String, Token> tokens2 = TokenMapperFactory.getLudoTokens();

            assertSame(tokens1, tokens2);
        }

        @Test
        void shouldNotContainPurpleTokenInLudo() {
            Map<String, Token> tokens = TokenMapperFactory.getLudoTokens();

            assertFalse(tokens.containsKey("PURPLE"));
            assertFalse(tokens.containsValue(Token.PURPLE));
        }
    }

    @Nested
    class SnlTokens {

        @Test
        void shouldReturnSnlTokensMap() {
            Map<String, Token> tokens = TokenMapperFactory.getSnlTokens();

            assertNotNull(tokens);
            assertEquals(5, tokens.size());
            assertEquals(Token.BLUE, tokens.get("BLUE"));
            assertEquals(Token.GREEN, tokens.get("GREEN"));
            assertEquals(Token.YELLOW, tokens.get("YELLOW"));
            assertEquals(Token.RED, tokens.get("RED"));
            assertEquals(Token.PURPLE, tokens.get("PURPLE"));
        }

        @Test
        void shouldReturnImmutableSnlTokensMap() {
            Map<String, Token> tokens = TokenMapperFactory.getSnlTokens();

            assertThrows(UnsupportedOperationException.class, () -> tokens.put("ORANGE", Token.BLUE));
        }

        @Test
        void shouldReturnSameSnlTokensMapOnMultipleCalls() {
            Map<String, Token> tokens1 = TokenMapperFactory.getSnlTokens();
            Map<String, Token> tokens2 = TokenMapperFactory.getSnlTokens();

            assertSame(tokens1, tokens2);
        }

        @Test
        void shouldContainPurpleTokenInSnl() {
            Map<String, Token> tokens = TokenMapperFactory.getSnlTokens();

            assertTrue(tokens.containsKey("PURPLE"));
            assertEquals(Token.PURPLE, tokens.get("PURPLE"));
        }
    }

    @Nested
    class IndividualLudoTokenRetrieval {

        @Test
        void shouldReturnCorrectLudoTokens() {
            assertEquals(Token.BLUE, TokenMapperFactory.getLudoToken("BLUE"));
            assertEquals(Token.GREEN, TokenMapperFactory.getLudoToken("GREEN"));
            assertEquals(Token.YELLOW, TokenMapperFactory.getLudoToken("YELLOW"));
            assertEquals(Token.RED, TokenMapperFactory.getLudoToken("RED"));
        }

        @Test
        void shouldReturnNullForInvalidLudoToken() {
            assertNull(TokenMapperFactory.getLudoToken("PURPLE"));
            assertNull(TokenMapperFactory.getLudoToken("ORANGE"));
            assertNull(TokenMapperFactory.getLudoToken("INVALID"));
        }

        @Test
        void shouldReturnNullForEmptyStringLudoToken() {
            assertNull(TokenMapperFactory.getLudoToken(""));
        }

        @Test
        void shouldBeCaseSensitiveForLudoTokens() {
            assertNull(TokenMapperFactory.getLudoToken("blue"));
            assertNull(TokenMapperFactory.getLudoToken("Blue"));
            assertNull(TokenMapperFactory.getLudoToken("BLUE "));
        }
    }

    @Nested
    class IndividualSnlTokenRetrieval {

        @Test
        void shouldReturnCorrectSnlTokens() {
            assertEquals(Token.BLUE, TokenMapperFactory.getSnlToken("BLUE"));
            assertEquals(Token.GREEN, TokenMapperFactory.getSnlToken("GREEN"));
            assertEquals(Token.YELLOW, TokenMapperFactory.getSnlToken("YELLOW"));
            assertEquals(Token.RED, TokenMapperFactory.getSnlToken("RED"));
            assertEquals(Token.PURPLE, TokenMapperFactory.getSnlToken("PURPLE"));
        }

        @Test
        void shouldReturnNullForInvalidSnlToken() {
            assertNull(TokenMapperFactory.getSnlToken("ORANGE"));
            assertNull(TokenMapperFactory.getSnlToken("INVALID"));
        }

        @Test
        void shouldReturnNullForEmptyStringSnlToken() {
            assertNull(TokenMapperFactory.getSnlToken(""));
        }

        @Test
        void shouldBeCaseSensitiveForSnlTokens() {
            assertNull(TokenMapperFactory.getSnlToken("purple"));
            assertNull(TokenMapperFactory.getSnlToken("Purple"));
            assertNull(TokenMapperFactory.getSnlToken("PURPLE "));
        }
    }

    @Nested
    class FactoryPattern {

        @Test
        void shouldNotBeInstantiable() {
            try {
                var constructor = TokenMapperFactory.class.getDeclaredConstructor();
                assertFalse(constructor.canAccess(null),
                        "Constructor should not be publicly accessible");
            } catch (NoSuchMethodException e) {
                // This is also acceptable - no default constructor
            }
        }

        @Test
        void shouldProvideStaticFactoryMethods() {
            // Verify that all expected methods exist and are static
            assertDoesNotThrow(() -> TokenMapperFactory.class.getDeclaredMethod("getLudoTokens"));
            assertDoesNotThrow(() -> TokenMapperFactory.class.getDeclaredMethod("getSnlTokens"));
            assertDoesNotThrow(() -> TokenMapperFactory.class.getDeclaredMethod("getLudoToken", String.class));
            assertDoesNotThrow(() -> TokenMapperFactory.class.getDeclaredMethod("getSnlToken", String.class));
        }
    }

    @Nested
    class EdgeCasesAndConsistency {

        @Test
        void shouldHaveConsistentTokensBetweenMapAndIndividualMethods() {
            Map<String, Token> ludoMap = TokenMapperFactory.getLudoTokens();

            for (Map.Entry<String, Token> entry : ludoMap.entrySet()) {
                Token fromMap = entry.getValue();
                Token fromMethod = TokenMapperFactory.getLudoToken(entry.getKey());
                assertEquals(fromMap, fromMethod,
                        "Inconsistency for token: " + entry.getKey());
            }
        }

        @Test
        void shouldHaveConsistentSnlTokensBetweenMapAndIndividualMethods() {
            Map<String, Token> snlMap = TokenMapperFactory.getSnlTokens();

            for (Map.Entry<String, Token> entry : snlMap.entrySet()) {
                Token fromMap = entry.getValue();
                Token fromMethod = TokenMapperFactory.getSnlToken(entry.getKey());
                assertEquals(fromMap, fromMethod,
                        "Inconsistency for SNL token: " + entry.getKey());
            }
        }

        @Test
        void shouldHaveLudoTokensAsSubsetOfSnlTokens() {
            Map<String, Token> ludoTokens = TokenMapperFactory.getLudoTokens();
            Map<String, Token> snlTokens = TokenMapperFactory.getSnlTokens();

            for (Map.Entry<String, Token> ludoEntry : ludoTokens.entrySet()) {
                assertTrue(snlTokens.containsKey(ludoEntry.getKey()),
                        "SNL should contain Ludo token: " + ludoEntry.getKey());
                assertEquals(ludoEntry.getValue(), snlTokens.get(ludoEntry.getKey()),
                        "Token values should match for: " + ludoEntry.getKey());
            }
        }

        @Test
        void shouldHaveCorrectTokenCounts() {
            assertEquals(4, TokenMapperFactory.getLudoTokens().size());
            assertEquals(5, TokenMapperFactory.getSnlTokens().size());

            // SNL should have exactly one more token than Ludo
            assertEquals(TokenMapperFactory.getLudoTokens().size() + 1,
                    TokenMapperFactory.getSnlTokens().size());
        }
    }
}