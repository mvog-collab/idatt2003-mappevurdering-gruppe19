package edu.ntnu.idatt2003.ui.navigation;

import edu.ntnu.idatt2003.exception.GameInitializationException;
import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.SnlGatewayFactory;
import edu.ntnu.idatt2003.ui.HomePage;
import edu.ntnu.idatt2003.ui.ludo.controller.LudoPageController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.snl.controller.SnlPageController;
import edu.ntnu.idatt2003.ui.snl.view.SnlPage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationService {

  private static final Logger LOG = Logger.getLogger(NavigationService.class.getName());
  private static NavigationService instance;
  private Stage primaryStage;
  private Scene homeSceneCache;
  private Scene previousGameSetupSceneCache;
  private String currentGameType;

  private NavigationService() {
  }

  public static synchronized NavigationService getInstance() {
    if (instance == null) {
      instance = new NavigationService();
      LOG.info("NavigationService instance created.");
    }
    return instance;
  }

  public void initialize(Stage primaryStage) {
    if (primaryStage == null) {
      LOG.severe("Primary stage cannot be null for NavigationService initialization.");
      throw new IllegalArgumentException("PrimaryStage cannot be null.");
    }
    this.primaryStage = primaryStage;
    LOG.info("NavigationService initialized with primary stage.");
  }

  public Stage getPrimaryStage() {
    if (primaryStage == null) {
      LOG.severe("NavigationService accessed before initialization.");
      throw new GameInitializationException("NavigationService not initialized with a Stage.");
    }
    return primaryStage;
  }

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
      // Potentially show an error dialog to the user or navigate to an error
      // page/home
      navigateToHome(); // Fallback
    }
  }

  public void navigateToSnlPage() {
    LOG.info("Navigating to Snakes & Ladders setup page.");
    if (getPrimaryStage() == null) {
      LOG.warning("Cannot navigate to SNL page, primary stage is null.");
      return;
    }
    try {
      CompleteBoardGame snlGateway = (CompleteBoardGame) SnlGatewayFactory.createDefault();
      SnlPage snlPage = new SnlPage();
      snlPage.connectToModel(snlGateway);
      new SnlPageController(snlPage, snlGateway);

      Scene snlPageScene = snlPage.getScene();
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

  public void navigateToGameScene(Scene gameBoardScene, String gameTitle) {
    LOG.info("Navigating to game scene: " + gameTitle);
    if (getPrimaryStage() == null || gameBoardScene == null) {
      LOG.warning("Cannot navigate to game scene, primary stage or gameBoardScene is null.");
      if (gameBoardScene == null)
        LOG.warning("gameBoardScene is null.");
      return;
    }
    gameBoardScene.setUserData("GAME_BOARD_SCENE");
    primaryStage.setScene(gameBoardScene);
    primaryStage.setTitle(gameTitle);
    LOG.info("Successfully navigated to game scene: " + gameTitle);
  }

  public void goBackToGameSetupPage() {
    LOG.info("Attempting to navigate back to game setup page for game type: " + currentGameType);
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
      LOG.info("Navigated back to previous game setup page: " + title);
    } else {
      LOG.warning("No previous game setup scene cached. Navigating to home.");
      navigateToHome();
    }
  }
}