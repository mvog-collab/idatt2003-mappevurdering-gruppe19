package edu.ntnu.idatt2003.ui.navigation;

import edu.ntnu.idatt2003.gateway.CompleteBoardGame;
import edu.ntnu.idatt2003.gateway.LudoGateway;
import edu.ntnu.idatt2003.gateway.SnlGatewayFactory;
import edu.ntnu.idatt2003.ui.HomePage;
import edu.ntnu.idatt2003.ui.ludo.controller.LudoPageController;
import edu.ntnu.idatt2003.ui.ludo.view.LudoPage;
import edu.ntnu.idatt2003.ui.snl.controller.SnlPageController;
import edu.ntnu.idatt2003.ui.snl.view.SnlPage;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationService {

  private static NavigationService instance;
  private Stage primaryStage;
  private Scene homeSceneCache;
  private Scene previousGameSetupSceneCache; // Stores LudoPage or SnlPage scene
  private String currentGameType; // "LUDO" or "SNL"

  private NavigationService() {}

  public static synchronized NavigationService getInstance() {
    if (instance == null) {
      instance = new NavigationService();
    }
    return instance;
  }

  public void initialize(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getPrimaryStage() {
    if (primaryStage == null) {
      throw new IllegalStateException("NavigationService not initialized with a Stage.");
    }
    return primaryStage;
  }

  private Scene getOrCreateHomeScene() {
    if (homeSceneCache == null) {
      HomePage homePageInstance = new HomePage();
      homeSceneCache = homePageInstance.createScene(primaryStage);
    }
    return homeSceneCache;
  }

  public void navigateToHome() {
    if (primaryStage == null) return;
    previousGameSetupSceneCache = null;
    currentGameType = null;
    primaryStage.setScene(getOrCreateHomeScene());
    primaryStage.setTitle("Retro Roll & Rise");
  }

  public void navigateToLudoPage() {
    if (primaryStage == null) return;
    CompleteBoardGame ludoGateway = LudoGateway.createDefault();
    LudoPage ludoPage = new LudoPage();
    ludoPage.connectToModel(ludoGateway);
    new LudoPageController(ludoPage, ludoGateway);

    Scene ludoPageScene = ludoPage.getScene();
    ludoPageScene.setUserData("LUDO_PAGE"); // Identify the scene
    this.previousGameSetupSceneCache = ludoPageScene;
    this.currentGameType = "LUDO";
    primaryStage.setScene(ludoPageScene);
    primaryStage.setTitle("Ludo - Setup");
  }

  public void navigateToSnlPage() {
    if (primaryStage == null) return;
    CompleteBoardGame snlGateway = (CompleteBoardGame) SnlGatewayFactory.createDefault();
    SnlPage snlPage = new SnlPage();
    snlPage.connectToModel(snlGateway);
    new SnlPageController(snlPage, snlGateway);

    Scene snlPageScene = snlPage.getScene();
    snlPageScene.setUserData("SNL_PAGE"); // Identify the scene
    this.previousGameSetupSceneCache = snlPageScene;
    this.currentGameType = "SNL";
    primaryStage.setScene(snlPageScene);
    primaryStage.setTitle("Snakes & Ladders - Setup");
  }

  public void navigateToGameScene(Scene gameBoardScene, String gameTitle) {
    if (primaryStage == null || gameBoardScene == null) return;
    // previousGameSetupSceneCache should already be set by navigateToLudoPage/navigateToSnlPage
    gameBoardScene.setUserData("GAME_BOARD_SCENE");
    primaryStage.setScene(gameBoardScene);
    primaryStage.setTitle(gameTitle);
  }

  public void goBackToGameSetupPage() {
    if (primaryStage == null) return;

    if (previousGameSetupSceneCache != null) {
      String title = "Game Setup";
      if ("LUDO".equals(currentGameType)) title = "Ludo - Setup";
      else if ("SNL".equals(currentGameType)) title = "Snakes & Ladders - Setup";
      primaryStage.setScene(previousGameSetupSceneCache);
      primaryStage.setTitle(title);
    } else {
      navigateToHome();
    }
  }
}
