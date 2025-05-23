package edu.ntnu.idatt2003.presentation.navigation;

import edu.ntnu.idatt2003.exception.GameInitializationException;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.SnlGatewayFactory;
import edu.ntnu.idatt2003.presentation.HomePage;
import edu.ntnu.idatt2003.presentation.ludo.controller.LudoPageController;
import edu.ntnu.idatt2003.presentation.ludo.view.LudoPage;
import edu.ntnu.idatt2003.presentation.snl.controller.SnlPageController;
import edu.ntnu.idatt2003.presentation.snl.view.SnlFrontPage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Singleton service for managing navigation between different game pages and
 * scenes.
 * <p>
 * This service handles scene transitions, caching, and stage management for the
 * game application. It provides centralized navigation logic and maintains
 * references to previously visited scenes for efficient back navigation.
 * </p>
 */
public class NavigationService {
  private static final Logger LOG = Logger.getLogger(NavigationService.class.getName());
  private static NavigationService instance;
  private Stage primaryStage;
  private Scene homeSceneCache;
  private Scene previousGameSetupSceneCache;
  private String currentGameType;

  /**
   * Private constructor for singleton pattern.
   */
  private NavigationService() {
  }

  /**
   * Gets the singleton instance of NavigationService.
   *
   * @return the NavigationService instance
   */
  public static synchronized NavigationService getInstance() {
    if (instance == null) {
      instance = new NavigationService();
      LOG.info("NavigationService instance created.");
    }
    return instance;
  }

  /**
   * Initializes the navigation service with the primary stage.
   *
   * @param primaryStage the main application stage
   * @throws IllegalArgumentException if primaryStage is null
   */
  public void initialize(Stage primaryStage) {
    if (primaryStage == null) {
      LOG.severe("Primary stage cannot be null for NavigationService initialization.");
      throw new IllegalArgumentException("PrimaryStage cannot be null.");
    }
    this.primaryStage = primaryStage;
    LOG.info("NavigationService initialized with primary stage.");
  }

  /**
   * Gets the primary stage for the application.
   *
   * @return the primary stage
   * @throws GameInitializationException if service not initialized
   */
  public Stage getPrimaryStage() {
    if (primaryStage == null) {
      LOG.severe("NavigationService accessed before initialization.");
      throw new GameInitializationException("NavigationService not initialized with a Stage.");
    }
    return primaryStage;
  }

  /**
   * Gets or creates the home scene, using caching for performance.
   *
   * @return the home scene
   * @throws GameInitializationException if service not initialized
   */
  private Scene getOrCreateHomeScene() {
    if (primaryStage == null) {
      LOG.warning("Attempted to get/create home scene but primaryStage is null.");
      throw new GameInitializationException(
          "NavigationService not initialized with a Stage, cannot create home scene.");
    }
    if (homeSceneCache == null) {
      LOG.info("Creating new HomeScene instance for cache.");
      HomePage homePageInstance = new HomePage();
      homeSceneCache = homePageInstance.createScene(primaryStage);
    }
    return homeSceneCache;
  }

  /**
   * Navigates to the home page and clears navigation history.
   */
  public void navigateToHome() {
    LOG.info("Navigating to Home page.");
    if (getPrimaryStage() == null) {
      LOG.warning("Cannot navigate to home, primary stage is null.");
      return;
    }
    previousGameSetupSceneCache = null;
    currentGameType = null;
    primaryStage.setScene(getOrCreateHomeScene());
    primaryStage.setTitle("Retro Roll & Rise");
  }

  /**
   * Navigates to the Ludo game setup page.
   * <p>
   * Creates a new Ludo gateway and page instance, then transitions to
   * the setup scene. Falls back to home page if navigation fails.
   * </p>
   */
  public void navigateToLudoPage() {
    LOG.info("Navigating to Ludo setup page.");
    if (getPrimaryStage() == null) {
      LOG.warning("Cannot navigate to Ludo page, primary stage is null.");
      return;
    }
    try {
      CompleteBoardGame ludoGateway = LudoGateway.createDefault();
      LudoPage ludoPage = new LudoPage();
      ludoPage.connectToModel(ludoGateway);
      new LudoPageController(ludoPage, ludoGateway);

      Scene ludoPageScene = ludoPage.getScene();
      ludoPageScene.setUserData("LUDO_PAGE");
      this.previousGameSetupSceneCache = ludoPageScene;
      this.currentGameType = "LUDO";
      primaryStage.setScene(ludoPageScene);
      primaryStage.setTitle("Ludo - Setup");
      LOG.info("Successfully navigated to Ludo setup page.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to navigate to Ludo page", e);
      navigateToHome();
    }
  }

  /**
   * Navigates to the Snakes & Ladders game setup page.
   * <p>
   * Creates a new SNL gateway and page instance, then transitions to
   * the setup scene. Falls back to home page if navigation fails.
   * </p>
   */
  public void navigateToSnlPage() {
    LOG.info("Navigating to Snakes & Ladders setup page.");
    if (getPrimaryStage() == null) {
      LOG.warning("Cannot navigate to SNL page, primary stage is null.");
      return;
    }
    try {
      CompleteBoardGame snlGateway = SnlGatewayFactory.createDefault();
      SnlFrontPage snlFrontPage = new SnlFrontPage();
      snlFrontPage.connectToModel(snlGateway);
      new SnlPageController(snlFrontPage, snlGateway);

      Scene snlPageScene = snlFrontPage.getScene();
      snlPageScene.setUserData("SNL_PAGE");
      this.previousGameSetupSceneCache = snlPageScene;
      this.currentGameType = "SNL";
      primaryStage.setScene(snlPageScene);
      primaryStage.setTitle("Snakes & Ladders - Setup");
      LOG.info("Successfully navigated to SNL setup page.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Failed to navigate to SNL page", e);
      navigateToHome(); // Fallback
    }
  }

  /**
   * Navigates to a game board scene with the specified title.
   *
   * @param gameBoardScene the scene containing the game board
   * @param gameTitle      the title to display for the game window
   */
  public void navigateToGameScene(Scene gameBoardScene, String gameTitle) {
    LOG.log(Level.INFO, "Navigating to game scene: {0}", gameTitle);
    if (getPrimaryStage() == null || gameBoardScene == null) {
      LOG.warning("Cannot navigate to game scene, primary stage or gameBoardScene is null.");
      if (gameBoardScene == null)
        LOG.warning("gameBoardScene is null.");
      return;
    }
    gameBoardScene.setUserData("GAME_BOARD_SCENE");
    primaryStage.setScene(gameBoardScene);
    primaryStage.setTitle(gameTitle);
    LOG.log(Level.INFO, "Successfully navigated to game scene: {0} ", gameTitle);
  }

  /**
   * Navigates back to the previously visited game setup page.
   * <p>
   * Uses cached scene reference for efficient navigation. Falls back
   * to home page if no previous scene is available.
   * </p>
   */
  public void goBackToGameSetupPage() {
    LOG.log(Level.INFO, "Attempting to navigate back to game setup page for game type: {0} ", currentGameType);
    if (getPrimaryStage() == null) {
      LOG.warning("Cannot go back, primary stage is null.");
      return;
    }

    if (previousGameSetupSceneCache != null) {
      String title = "Game Setup";
      if ("LUDO".equals(currentGameType))
        title = "Ludo - Setup";
      else if ("SNL".equals(currentGameType))
        title = "Snakes & Ladders - Setup";
      primaryStage.setScene(previousGameSetupSceneCache);
      primaryStage.setTitle(title);
      LOG.log(Level.INFO, "Navigated back to previous game setup page: {0}", title);
    } else {
      LOG.warning("No previous game setup scene cached. Navigating to home.");
      navigateToHome();
    }
  }
}