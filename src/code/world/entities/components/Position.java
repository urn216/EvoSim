package code.world.entities.components;

import code.world.Scene;
import mki.math.MathHelp;
import mki.math.vector.Vector2;

public abstract class Position {
  protected Vector2 position;

  protected Position(Vector2 position) {
    this.position = position;
  }

  public Vector2 getPosition() {
    return position;
  }

  public abstract void setPosition(Vector2 position);
  public abstract void offsetPosition(Vector2 offset);

  public static class SquareBounded extends Position {
    public SquareBounded(Vector2 position) {super(position);}

    @Override
    public void setPosition(Vector2 position) {
      int halfWidth = Scene.getMapWidth()/2, halfHeight = Scene.getMapHeight()/2;
      this.position = new Vector2(MathHelp.clamp(position.x, -halfWidth, halfWidth), MathHelp.clamp(position.y, -halfHeight, halfHeight));
    }

    @Override
    public void offsetPosition(Vector2 offset) {
      int halfWidth = Scene.getMapWidth()/2, halfHeight = Scene.getMapHeight()/2;
      this.position = new Vector2(MathHelp.clamp(this.position.x+offset.x, -halfWidth, halfWidth), MathHelp.clamp(this.position.y+offset.y, -halfHeight, halfHeight));
    }
  }

  public static class CircleBounded extends Position {
    public CircleBounded(Vector2 position) {super(position);}

    @Override
    public void setPosition(Vector2 position) {
      this.position = position;
      if (this.position.magsquare() > Scene.getMapRadius()*Scene.getMapRadius()) {
        this.position = this.position.scale(Scene.getMapRadius()/this.position.magnitude());
      }
    }

    @Override
    public void offsetPosition(Vector2 offset) {
      this.position = this.position.add(offset);
      if (this.position.magsquare() > Scene.getMapRadius()*Scene.getMapRadius()) {
        this.position = this.position.scale(Scene.getMapRadius()/this.position.magnitude());
      }
    }
  }

  @Override
  public String toString() {
    return this.position.toString();
  }
}
