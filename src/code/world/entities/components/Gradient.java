package code.world.entities.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;

import mki.math.vector.Vector2;

public class Gradient {

  private static final float[] fs = {0.5f, 1f};
  
  private static final Ellipse2D body = new Ellipse2D.Double();

  private final Color[] cs;

  public Gradient(int baseColour, int innerTransparency, int outerTransparency) {
    this.cs = new Color[] {
      new Color(baseColour&((innerTransparency<<24)|~(255<<24)), true), 
      new Color(baseColour&((outerTransparency<<24)|~(255<<24)), true)
    };
  }

  public final void draw(Graphics2D g, Vector2 center, double radius) {
    double left = center.x-radius;
    double up   = center.y-radius;
    double diam = radius*2+1;

    body.setFrame(left, up, diam, diam);

    g.setPaint(new RadialGradientPaint((float)center.x, (float)center.y, (float)radius, fs, cs));
    g.fill(body);
  }
}
