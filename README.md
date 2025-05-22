STUDENT NAMES = "Martha Luise Vogel and Edvard Emmanuel Klavenes"

# Snakes & Ladders & Ludo – Java 21 Game Engine

Course project (IDATT2003, spring 2025). We built a **modular game engine** that
runs both **Ludo** and **Snakes & Ladders**.

**Key objectives**

* SOLID‑friendly architecture
* Domain‑specific exception hierarchy
* Clean separation: *engine ↔ gateway ↔ UI*

---

## 📖 Project overview

The engine handles boards, rules, dice, players and events.  A gateway layer
adapts the engine to a JavaFX client.  Everything is plain **Java 21** and
built with Maven.

---

## 📂 Project structure

### Packages (`src/main/java`)

| Package                            | Responsibility             | Core classes                                                |
| ---------------------------------- | -------------------------- | ----------------------------------------------------------- |
| **`edu.games.engine.*`**           | Pure game / rule logic     | `DefaultGame`, `LinearBoard`, `LudoPath`, exception classes |
| **`edu.ntnu.idatt2003.gateway.*`** | Bridge between engine & UI | `LudoGateway`, `SnlGateway`, observer events                |
| **`edu.ntnu.idatt2003.ui.*`**      | JavaFX front‑end           | `LudoBoardController`, `SnakesAndLaddersApp`                |

### Test classes (`src/test/java`)

* `LinearBoardTest`, `RandomDiceTest`, `DefaultGameTest`, …
* **New:** `DefaultGameRuleViolationTest` – verifies that the engine throws a
  `RuleViolationException` when the game is already finished.

---

## How to run the project

```bash
# 1 clone
git clone <repo‑url>
cd snakes-and-ladders

# 2 build + run tests
mvn clean test      # → BUILD SUCCESS

# 3 launch the Ludo JavaFX client
mvn javafx:run
```

*Requirements*: JDK 21 + Maven 3.9+

Run the engine only (no UI):

```bash
mvn -q exec:java -Dexec.mainClass="edu.ntnu.scripts.RunEngineDemo"
```

---

## How to run the tests

All JUnit 5 tests:

```bash
mvn test
```

---

## 🔗 Repository

[https://github.com/NTNU-IDI/idatt2003-gameengine-gr19](https://github.com/NTNU-IDI/idatt2003-gameengine-gr19)  *(placeholder)*

---

## Licence

MIT © 2025 Martha Luise Vogel, Edvard Emmanuel Klavenes & Group 19

---

## Credits / inspiration

* [https://www.makeareadme.com/](https://www.makeareadme.com/) for quick README tips
* Oracle Java Docs & JavaFX tutorials
* *Clean Architecture* – Robert C. Martin
