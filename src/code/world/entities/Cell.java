package code.world.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import mki.math.vector.Vector2;
import code.world.Scene;
import code.world.entities.components.Gradient;
import code.world.entities.components.Position;

public class Cell {

  private final Position position;
  private final Gradient body;
  private final int radius;

  public static final Cell random() {
    int rad = (int)(Math.random()*10+5);
    int baseColour = Color.HSBtoRGB((float)(Math.random()*0.5), (float)Math.random(), 0.7f);
    return new Cell(
      Vector2.fromAngle(Math.random()*2*Math.PI, Math.sqrt(Math.random())*(Scene.getMapRadius()-rad)),
      baseColour,
      rad
    );
  }

  public Cell(Vector2 position, int baseColour, int radius) {
    this.position = new Position.CircleBounded(position);
    this.body = new Gradient(baseColour, 100, 240);
    this.radius = radius;
  }

  public Vector2 getPosition() {
    return position.getPosition();
  }

  public int getRadius() {
    return radius;
  }

  public void setPosition(Vector2 position) {
    this.position.setPosition(position);
  }

  public void offsetPosition(Vector2 offset) {
    this.position.offsetPosition(offset);
  }

  public void update() {
    offsetPosition(Vector2.fromAngle(Math.random()*2*Math.PI, 1));
  }

  public void draw(Graphics2D g) {
    body.draw(g, Scene.getCam().worldToScreenV(getPosition()), radius*Scene.getCam().getZoom());
  }
}
