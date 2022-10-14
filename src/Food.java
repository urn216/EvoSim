import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
// import java.awt.geom.Line2D;
// import java.awt.geom.Rectangle2D;

/**
 * Write a description of class Food here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Food
{
    // instance variables - replace the example below with your own
    private int[] colour = {50, 240, 0};
    private double x;
    private double y;
    private double energy;
    private double size;

    /**
     * Constructor for objects of class Food
     */
    public Food(double X, double Y, double eP)
    {
        this.x = X;
        this.y = Y;
        this.energy = eP;
        size = energy/10;
    }

    public void setEP(double newEP) {
        this.energy = newEP;
        size = energy/10;
    }

    public int[] getCol() {
        return this.colour;
    }

    public double[] getStats() {
        double[] stats = {this.energy, this.x, this.y};
        return stats;
    }

    public String toString() {
        return "Food "+this.x+" "+this.y+" "+this.energy;
    }

    public void draw(Graphics2D g, int conX, int conY) {
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(colour[0], colour[1], colour[2]));
        g.fill(new Ellipse2D.Double(x+conX-size, y+conY-size, 2*size, 2*size));
        g.setColor(new Color(colour[0], colour[1]-20, colour[2]));
        g.draw(new Ellipse2D.Double(x+conX-size, y+conY-size, 2*size, 2*size));
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(1));
    }
}
