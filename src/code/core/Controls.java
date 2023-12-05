package code.core;

import java.awt.MouseInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;

import code.world.Scene;
import code.world.entities.Camera;
import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.ui.control.UIController;
import mki.ui.control.UIState;

/**
 * Handles all user input within the game
 */
abstract class Controls {
  
  public static final boolean[] KEY_DOWN = new boolean[65536];
  public static final boolean[] MOUSE_DOWN = new boolean[Math.max(MouseInfo.getNumberOfButtons(), 3)];
  
  public static Vector2I mousePos = new Vector2I();
  public static Vector2I mousePre = new Vector2I();
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  public static void initialiseControls(JFrame FRAME) {
    
    //Mouse Controls
    FRAME.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        updateMousePos(e);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        updateMousePos(e);
      }
    });
    FRAME.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        updateMousePos(e);

        if (UIController.getHighlightedInteractable() == null) MOUSE_DOWN[e.getButton()] = true;
        mousePre = mousePos;
        
        //left click
        if (e.getButton() == 1) {
          if (UIController.press()) return; //press something
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        updateMousePos(e);
        
        MOUSE_DOWN[e.getButton()] = false;
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
          //press something
        }
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        mousePos = new Vector2I(Core.WINDOW.screenWidth()/2, Core.WINDOW.screenHeight()/2);
      }
    });

    FRAME.addMouseWheelListener(new MouseAdapter() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (!UIController.isState(UIState.DEFAULT)) return;

        Camera cam = Scene.getCam();
        
        cam.setResolution(Core.WINDOW.screenWidth(), Core.WINDOW.screenHeight());

        //zooming into the window
        if (KEY_DOWN[KeyEvent.VK_CONTROL] || KEY_DOWN[KeyEvent.VK_META]) {
          cam.scaleZoom(
            e.getWheelRotation()<0 ? 1.25 : 0.8, 
            cam.screenToWorldV(mousePos)
          );
          return;
        }
        
        //scrolling along the window
        cam.offsetPosition( e.isShiftDown() ?
          new Vector2(e.getPreciseWheelRotation()*50, 0) :
          new Vector2(0, e.getPreciseWheelRotation()*50)
        );
      }
    });
    
    //Keyboard Controls
    FRAME.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (UIController.getActiveTextfield() != null && !KEY_DOWN[KeyEvent.VK_CONTROL]) UIController.typeKey(e);
        
        if(KEY_DOWN[keyCode]) return; //Key already in
        KEY_DOWN[keyCode] = true;
        
        // System.out.print(keyCode);
        
        switch (keyCode) {
          case KeyEvent.VK_F11:
          Core.WINDOW.toggleFullscreen();
          break;
          case KeyEvent.VK_ESCAPE:
          UIController.release();
          UIController.back();
          break;
          case KeyEvent.VK_ENTER:
          UIController.press();
          break;
          case KeyEvent.VK_SPACE:
          if (UIController.getState() == UIState.DEFAULT) Core.pause();
          break;
          default:
          break;
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        KEY_DOWN[keyCode] = false;
        
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.release();
        }
      }
    });
  }
  
  /**
  * Updates the program's understanding of the location of the mouse cursor after a supplied {@code MouseEvent}.
  * 
  * @param e the {@code MouseEvent} to determine the cursor's current position from
  */
  public static void updateMousePos(MouseEvent e) {
    int x = e.getX() - Core.WINDOW.toolBarLeft;
    int y = e.getY() - Core.WINDOW.toolBarTop;
    mousePos = new Vector2I(x, y);
    
    UIController.cursorMove(mousePos);
  }

  /**
  * moves the camera around the scene
  */
  public static void cameraMovement() {
    if (!UIController.isState(UIState.DEFAULT)) return;

    Camera cam = Scene.getCam();

    if (MOUSE_DOWN[2] || MOUSE_DOWN[3] || (MOUSE_DOWN[1] && KEY_DOWN[KeyEvent.VK_META])) {
      cam.offsetPosition(mousePos.subtract(mousePre).scale(-1/cam.getZoom()));
      mousePre = mousePos;
      return;
    }

    //Left
    if (
      KEY_DOWN[KeyEvent.VK_LEFT    ] || 
      KEY_DOWN[KeyEvent.VK_A       ]
    ) cam.offsetPosition(new Vector2(-10,   0));
    //Up
    if (
      KEY_DOWN[KeyEvent.VK_UP      ] || 
      KEY_DOWN[KeyEvent.VK_W       ]
    ) cam.offsetPosition(new Vector2(  0, -10));
    //Right
    if (
      KEY_DOWN[KeyEvent.VK_RIGHT   ] || 
      KEY_DOWN[KeyEvent.VK_D       ]
    ) cam.offsetPosition(new Vector2( 10,   0));
    //Down
    if (
      KEY_DOWN[KeyEvent.VK_DOWN    ] || 
      KEY_DOWN[KeyEvent.VK_S       ]
    ) cam.offsetPosition(new Vector2(  0,  10));
    //in
    if (
      KEY_DOWN[KeyEvent.VK_EQUALS  ] || 
      KEY_DOWN[KeyEvent.VK_ADD     ]
    ) cam.scaleZoom(1.05);
    //out
    if (
      KEY_DOWN[KeyEvent.VK_MINUS   ] || 
      KEY_DOWN[KeyEvent.VK_SUBTRACT]
    ) cam.scaleZoom(1/1.05);
  }
}
