package code.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import code.world.Scene;
import mki.ui.control.UIColours;
import mki.ui.control.UIController;
import mki.ui.control.UIState;
import mki.ui.elements.ElemCanvas;

public abstract class Core {
  
  public static final Window WINDOW = new Window(
    "Evo Sim",
    (screenSizeX, screenSizeY) -> getGameWindow().setImageDimensions((int)(0.55 * screenSizeX), (int)(0.9 * screenSizeY))
  );
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static boolean quit = false;
  private static boolean play = false;

  private static ElemCanvas gameWindow = UICreator.canvas;

  static {
    WINDOW.setFullscreen(false);

    UIController.addColourTheme("EVO", new UIColours.ColourSet(
      new Color(180, 180, 180), 
      new Color(0, 0, 0, 0), 
      new Color(200, 200, 220), 
      new Color(220, 220, 220), 
      new Color(80, 80, 80), 
      new Color(255, 165, 0), 
      new Color(200, 125, 0), 
      new Color(80, 80, 80)
    ));
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.setCurrentPane("Main Menu");
    
    Controls.initialiseControls(WINDOW.FRAME);
  }
  
  public static void main(String[] args) {
    run();
  }
  
  public static ElemCanvas getGameWindow() {
    return gameWindow;
  }

  public static void pause() {
    Core.play = !Core.play;
    UICreator.playButton.setText(Core.play ? "Pause" : "Resume");
  }

  public static void newGame() {
    Core.play = false;
    UICreator.playButton.setText("Play");
    UIController.setState(UIState.NEW_GAME);
    Scene.clear();
  }

  public static void begin() {
    Core.play = true;
    UICreator.playButton.setText("Pause");
    UIController.setState(UIState.DEFAULT);
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }
  
  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  private static void run() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      
      if (quit) {
        System.exit(0);
      }

      Controls.cameraMovement();
      
      if (play) Scene.update();
      
      WINDOW.PANEL.repaint();
      tickTime = System.currentTimeMillis() - tickTime;
      if (tickTime > MILLISECONDS_PER_TICK) System.out.println("Uh oh");
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }
  
  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    Graphics2D sg = (Graphics2D)gra;
    Graphics2D cg = Core.gameWindow.getImage().createGraphics();

    int sw = WINDOW    .  screenWidth(), sh = WINDOW    .  screenHeight();
    int cw = gameWindow.getImageWidth(), ch = gameWindow.getImageHeight();
    
    sg.setColor(UIColours.ACTIVE.buttonBodyLocked());
    sg.fillRect(0, 0, sw, sh);

    cg.setColor(UIColours.ACTIVE.background());
    cg.fillRect(0, 0, cw, ch);

    Scene       .draw(cg, cw, ch);
    UIController.draw(sg, sw, sh);

    cg.dispose();
  }
}