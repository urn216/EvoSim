package code.world.entities;

import code.world.entities.components.Position;
import mki.math.MathHelp;
import mki.math.vector.Vector2;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera {
  private static final double CLOSE_MAGNITUDE = 0.075;

  private static final double ZOOM_BOUND_U  = 15;
  private static final double ZOOM_BOUND_L  = 0.85;

  private final float defaultHalfHeight;
  
  private double defaultZoom;
  private double zoom;
  private Position position;
  private Position target;

  private float screenHalfWidth;
  private float screenHalfHeight;

  /**
  * @Camera
  *
  * Constructs a camera with an x position, a y position, a default zoom level, and the current resolution of the game window.
  */
  public Camera(Vector2 worldPos, double zoom, int screenHeight) {
    this.position = new Position.CircleBounded(worldPos);
    this.defaultZoom = zoom;
    this.zoom = zoom;
    this.screenHalfWidth  = screenHeight/2f;
    this.screenHalfHeight = screenHeight/2f;
    this.defaultHalfHeight= this.screenHalfHeight;
  }

  public Vector2 getPosition() {
    return position.getPosition();
  }

  public Position getTarget() {
    return target;
  }

  public double getZoom() {
    return (this.screenHalfHeight/this.defaultHalfHeight)*zoom;
  }

  public double getDefaultZoom() {
    return defaultZoom;
  }

  public void resetZoom() {this.zoom = defaultZoom;}

  public void setPosition(Vector2 position) {
    this.position.setPosition(position);
  }

  public void offsetPosition(Vector2 offset) {
    this.position.offsetPosition(offset);
  }

  public void setResolution(int screenWidth, int screenHeight) {
    this.screenHalfWidth  = screenWidth /2f;
    this.screenHalfHeight = screenHeight/2f;
  }

  public void setTarget(Position t){
    target = t;
  }

  public void setZoom(double zoom) {
    this.zoom = MathHelp.clamp(zoom, ZOOM_BOUND_L, ZOOM_BOUND_U);
  }

  public void setZoom(double zoom, Vector2 focalPoint) {
    zoom = MathHelp.clamp(zoom, ZOOM_BOUND_L, ZOOM_BOUND_U);
    setPosition(focalPoint.add(this.position.getPosition().subtract(focalPoint).scale(2-zoom/this.zoom)));
    this.zoom = zoom;
  }

  public void scaleZoom(double scale) {
    this.zoom = MathHelp.clamp(this.zoom*scale, ZOOM_BOUND_L, ZOOM_BOUND_U);
  }

  public void scaleZoom(double scale, Vector2 focalPoint) {
    double zoom = MathHelp.clamp(this.zoom*scale, ZOOM_BOUND_L, ZOOM_BOUND_U);
    setPosition(focalPoint.add(this.position.getPosition().subtract(focalPoint).scale(2-zoom/this.zoom)));
    this.zoom = zoom;
  }

  public void follow() {
    if (target == null) return;

    Vector2 dist = new Vector2(target.getPosition().subtract(position.getPosition()));
    if (dist.magsquare() >= 0.1)
      offsetPosition(dist.scale(CLOSE_MAGNITUDE));
  }

  /**
   * Generates a constant to convert any world-space x-coordinate into a screen-space 
   * x-coordinate as viewed through this {@code Camera}.
   * <p>
   * To use, a world-space x-coordinate must be multiplied by the camera's current zoom 
   * and have this constant subtracted from the resulting product.
   * 
   * @return a constant for use in converting an x-coordinate from world-space to screen-space
   */
  @Deprecated
  public double conX() {
    return position.getPosition().x*getZoom()-this.screenHalfWidth;
  }

  /**
   * Generates a constant to convert any world-space y-coordinate into a screen-space 
   * y-coordinate as viewed through this {@code Camera}.
   * <p>
   * To use, a world-space y-coordinate must be multiplied by the camera's current zoom 
   * and have this constant subtracted from the resulting product.
   * 
   * @return a constant for use in converting an y-coordinate from world-space to screen-space
   */
  @Deprecated
  public double conY() {
    return position.getPosition().y*getZoom()-this.screenHalfHeight;
  }

  public double  worldToScreenX(double  x) {
    return (x-position.getPosition().x)*getZoom()+this.screenHalfWidth;
  }

  public double  worldToScreenY(double  y) {
    return (y-position.getPosition().y)*getZoom()+this.screenHalfHeight;
  }

  public Vector2 worldToScreenV(Vector2 v) {
    return v.subtract(position.getPosition()).scale(getZoom()).add(this.screenHalfWidth, this.screenHalfHeight);
  }

  public double  screenToWorldX(double  x) {
    return (x-this.screenHalfWidth)/getZoom()+position.getPosition().x;
  }

  public double  screenToWorldY(double  y) {
    return (y-this.screenHalfHeight)/getZoom()+position.getPosition().y;
  }

  public Vector2 screenToWorldV(Vector2 v) {
    return v.subtract(this.screenHalfWidth, this.screenHalfHeight).scale(1/getZoom()).add(position.getPosition());
  }
  
  //Save constant whenever camera elements change?

  /**
   * Checks to see if an object is currently visible within the bounds of a camera
   * 
   * @param leftWorldBound  the left-most extent of the object within world-space
   * @param upperWorldBound the top-most extent of the object within world-space
   * @param rightWorldBound the right-most extent of the object within world-space
   * @param lowerWorldBound the bottom-most extent of the object within world-space
   * 
   * @return {@code true} if the given bounds lie within the frame of the camera
   */
  public boolean canSee(double leftWorldBound, double upperWorldBound, double rightWorldBound, double lowerWorldBound) {
    double zoom = getZoom();

    return (
      ( leftWorldBound-position.getPosition().x)*zoom <   this.screenHalfWidth  && 
      (upperWorldBound-position.getPosition().y)*zoom <   this.screenHalfHeight && 
      (rightWorldBound-position.getPosition().x)*zoom >= -this.screenHalfWidth  && 
      (lowerWorldBound-position.getPosition().y)*zoom >= -this.screenHalfHeight
    );
  }

  public boolean canSee(Vector2 centerWorldPos, double radius) {
    double zoom = getZoom();

    return (
      (Math.abs((centerWorldPos.x-position.getPosition().x))-radius)*zoom < this.screenHalfWidth  && 
      (Math.abs((centerWorldPos.y-position.getPosition().y))-radius)*zoom < this.screenHalfHeight
    );
  }
}
