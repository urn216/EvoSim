package code.core;

import code.world.Scene;
import mki.math.vector.Vector2;
import mki.ui.control.UIPane;
import mki.ui.control.UIState;
import mki.ui.components.UIComponent;
import mki.ui.components.interactables.*;

import mki.ui.elements.*;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.07;
  private static final double BUFFER_HEIGHT = 0.012;

  static final UIButton playButton = new UIButton("Play", Core::pause);

  static final ElemCanvas canvas = new ElemCanvas(
    new Vector2(0.219, 0.038), 
    new Vector2(0.781, 0.962), 
    BUFFER_HEIGHT, 
    UIElement.TRANSITION_SHRINK, 
    (int)(0.55 * Core.WINDOW.screenWidth ()),
    (int)(0.9  * Core.WINDOW.screenHeight())
  );

  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement outPanel = leftList(
      playButton,
      new UIToggle("Fast Mode"      , () -> false, (b) -> {}),
      new UIToggle("Show Stats"     , () -> false, (b) -> {}),
      new UIToggle("Auto Focus"     , () -> false, (b) -> {}),
      new UIToggle("Control Focus"  , () -> false, (b) -> {}),
      new UIButton("New Simulation" , Core::newGame),
      new UIButton("Save Simulation", () -> {}),
      new UIButton("Load Simulation", () -> {}),
      new UIToggle("Full Screen"    , Core.WINDOW::isFullScreen, Core.WINDOW::setFullscreen),
      new UIButton("Quit to Desktop", Core::quitToDesk)
    );

    UIElement newGame = leftList(
      new UIButton("Begin", Core::begin),
      new UISlider.Integer("Plate Radius: %.0f", Scene::getMapRadius, Scene::setMapRadius, 100, 1000, 50)
    );

    mainMenu.addState(UIState.DEFAULT,  outPanel);
    mainMenu.addState(UIState.DEFAULT,  canvas  );
    mainMenu.addState(UIState.NEW_GAME, newGame  , UIState.DEFAULT);
    mainMenu.addState(UIState.NEW_GAME, canvas  );

    mainMenu.clear();
    
    return mainMenu;
  }

  private static UIElement leftList(UIComponent... components) {
    return new ElemListVert(
      new Vector2(0  , 0),
      new Vector2(0.2, 1),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_LEFT
    );
  }
}
