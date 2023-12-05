package code.world;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import code.world.entities.Camera;
import code.world.entities.Cell;
import code.world.entities.components.Gradient;
import mki.math.MathHelp;
import mki.math.vector.Vector2;
import mki.ui.control.UIColours;

public abstract class Scene {
  private static final Ellipse2D.Double CIRCLE = new Ellipse2D.Double();
  private static final Gradient PLATE_GRADIENT = new Gradient(UIColours.ACTIVE.buttonBody().getRGB(), 160, 255);
  private static final Vector2 MIDDLE = new Vector2();

  private static final List<Cell> CELLS = new ArrayList<>();

  private static int mapRadius  = 250;
  private static int borderStep = 4;

  private static Camera cam = new Camera(MIDDLE, 1, mapRadius*2);

  public static Camera getCam() {
    return cam;
  }

  public static int getMapRadius() {
    return mapRadius;
  }

  public static int getMapWidth() {
    return mapRadius*2;
  }

  public static int getMapHeight() {
    return mapRadius*2;
  }

  public static void setMapRadius(int mapRadius) {
    Scene.mapRadius = mapRadius;
    cam = new Camera(MIDDLE, 1, mapRadius*2);
  }

  public static void clear() {
    CELLS.clear();
    cam.setPosition(MIDDLE);
    cam.resetZoom();
  }

  public static void update() {
    if(CELLS.size() < mapRadius) CELLS.add(Cell.random());

    for (int i = 0; i < CELLS.size(); i++) {
      CELLS.get(i).update();
    }
  }

  public static void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    cam.setResolution(screenSizeX, screenSizeY);

    double z = cam.getZoom();
    float scaledRadius = (float)z*mapRadius;
    Vector2 center = cam.worldToScreenV(MIDDLE);

    g.setStroke(new BasicStroke());
    PLATE_GRADIENT.draw(g, center, scaledRadius);

    for (int i = 0; i < CELLS.size(); i++) {
      Cell c = CELLS.get(i);
      if (cam.canSee(c.getPosition(), c.getRadius())) c.draw(g);
    }

    float circCorner = scaledRadius*(float)MathHelp.INVERSE_ROOT_TWO;
    g.setStroke(new BasicStroke((float)(borderStep*3*z)));

    scaledRadius+=borderStep*z;
    g.setPaint(new GradientPaint(
      (float)center.x-circCorner, (float)center.y-circCorner, UIColours.ACTIVE.buttonAccentOut(), 
      (float)center.x+circCorner, (float)center.y+circCorner, UIColours.ACTIVE.buttonBodyLocked()
    ));
    CIRCLE.setFrame(cam.worldToScreenX(-mapRadius-borderStep  ), cam.worldToScreenY(-mapRadius-borderStep  ), scaledRadius*2, scaledRadius*2);
    g.draw(CIRCLE);
    
    scaledRadius+=borderStep*2*z;
    g.setPaint(new GradientPaint(
      (float)center.x-circCorner, (float)center.y-circCorner, UIColours.ACTIVE.buttonBodyLocked(), 
      (float)center.x+circCorner, (float)center.y+circCorner, UIColours.ACTIVE.buttonAccentOut()
    ));
    CIRCLE.setFrame(cam.worldToScreenX(-mapRadius-borderStep*3), cam.worldToScreenY(-mapRadius-borderStep*3), scaledRadius*2, scaledRadius*2);
    g.draw(CIRCLE);
  }
}
