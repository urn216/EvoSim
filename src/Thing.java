import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
// import java.awt.geom.Rectangle2D;

/**
 * The creature class
 */
public class Thing
{
    private int[] colour = {0, 0, 0};
    private Color accCol;
    private double gen;
    private double x;
    private double y;
    private double energy;
    private double replicateChance;
    private double mutateChance;
    private double angle;
    private double turnS;
    private double speed;
    private double size;
    private double multi;
    private int mapSize = Core.mapSize;

    private boolean controller = false;
    private double tarX = -1;
    private double tarY = -1;

    /**
     * Constructor for objects of class Thing
     */
    public Thing(int R, int G, int B, double X, double Y, double eP, double rC, double ang, double angt, double v, double s, double m, double mC, double gener)
    {
        double colC = Math.random();
        this.colour[0] = colC < mC ? (int) Math.round((R-32)+Math.random()*64) : R;
        if (0 > this.colour[0] || this.colour[0] > 255) {this.colour[0] = R;};
        this.colour[1] = colC < mC ? (int) Math.round((G-32)+Math.random()*64) : G;
        if (0 > this.colour[1] || this.colour[1] > 255) {this.colour[1] = G;};
        this.colour[2] = colC < mC ? (int) Math.round((B-32)+Math.random()*64) : B;
        if (0 > this.colour[2] || this.colour[2] > 255) {this.colour[2] = B;};
        float[] hsb = {0, 0, 0};
        accCol = Color.RGBtoHSB(colour[0], colour[1], colour[2], hsb)[2] < 0.4 ? Color.getHSBColor(hsb[0], hsb[1], hsb[2]+0.2F) : Color.getHSBColor(hsb[0], hsb[1], hsb[2]-0.2F);
        this.x = X;
        this.y = Y;
        this.energy = eP;
        this.replicateChance = Math.random() < mC ? (rC-0.1)+Math.random()*0.2 : rC;
        if (0 > this.replicateChance || this.replicateChance > 0.5) {this.replicateChance = rC;};
        this.mutateChance = Math.random() < mC ? (mC-0.025)+Math.random()*0.05 : mC;
        if (0 > this.mutateChance || this.mutateChance > 1) {this.mutateChance = mC;};
        this.angle = ang;
        this.turnS = Math.random() < mC ? (angt-10)+Math.random()*20 : angt;
        if (0 > this.turnS || this.turnS > 180) {this.turnS = angt;};
        this.speed = Math.random() < mC ? (v-1)+Math.random()*2 : v;
        if (0 > this.speed || this.speed > 10) {this.speed = v;};
        this.size = Math.random() < mC ? (s-2)+Math.random()*4 : s;
        if (2 > this.size || this.size > 14) {this.size = s;};
        this.multi = Math.random() < mC ? Math.random()*4 : m;
        this.gen = gener + 1;
    }

    public void move() {
        if (controller && tarX != -1 && tarY != -1) {
            playerMove();
        }
        else {
            autoMove();
        }
        if (this.x < size) {this.x = size;};
        if (this.x > mapSize-size) {this.x = mapSize-size;};
        if (this.y < size) {this.y = size;};
        if (this.y > mapSize-size) {this.y = mapSize-size;};
    }

    private void autoMove() {
        this.angle += ((-turnS/2)+Math.random()*turnS);
        if (this.angle < 0) {
            this.angle += 360;
        }
        if (this.angle >= 360) {
            this.angle -= 360;
        }
        double xPercent = Math.sin(Math.toRadians(this.angle));
        double yPercent = Math.cos(Math.toRadians(this.angle));
        this.x += (Math.random()*xPercent*speed);
        this.y += (Math.random()*yPercent*speed);
    }

    private void playerMove() {
        double dX = tarX-x;
        double dY = tarY-y;
        double tarAng = Math.toDegrees(Math.atan(dX/dY));
        if (dY < 0) {
            tarAng += 180;
        }
        else if (dX < 0 && dY >= 0) {
            tarAng += 360;
        }
        if ((angle-180 < tarAng && angle > tarAng) || (angle-180 < tarAng-360 && angle > tarAng-360)) {
            if ((angle-turnS/2 <= tarAng && angle >= tarAng) || (angle-turnS/2 <= tarAng-360 && angle >= tarAng-360)) {
                angle = tarAng;
            }
            else {
                angle -= turnS/2;
            }
            if (angle < 0) {
                angle += 360;
            }
        }
        else {
            if ((angle+turnS/2 >= tarAng && angle <= tarAng) || (angle+turnS/2 >= tarAng+360 && angle <= tarAng+360)) {
                angle = tarAng;
            }
            else {
                angle += turnS/2;
            }
            if (this.angle >= 360) {
                this.angle -= 360;
            }
        }
        double xPercent = Math.sin(Math.toRadians(this.angle));
        double yPercent = Math.cos(Math.toRadians(this.angle));
        this.x += (xPercent*speed*(0.2*Math.sqrt(Math.abs(dX))));
        this.y += (yPercent*speed*(0.2*Math.sqrt(Math.abs(dY))));
    }

    public void setCont() {
        controller = true;
    }

    public void unsetCont() {
        controller = false;
    }

    public void setTarget(double oX, double oY) {
        if (controller) {
            tarX = oX;
            tarY = oY;
        }
        else {
            tarX = -1;
            tarY = -1;
        }
    }

    public boolean touching(double oX, double oY) {
        if (oX>=x-size && oX<=x+size && oY>=y-size && oY<=y+size) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setEP(double newEP) {
        this.energy = newEP;
    }

    public int[] getCol() {
        return this.colour;
    }

    public double[] getStats() {
        double[] stats = {this.energy, this.replicateChance, this.x, this.y, this.angle, this.turnS, this.speed, size, multi, mutateChance, gen};
        return stats;
    }

    public String toString() {
        return "Thing "+this.colour[0]+" "+this.colour[1]+" "+this.colour[2]+" "+this.x+" "+this.y+" "+this.energy+" "+this.replicateChance+" "+this.angle+" "+this.turnS+" "+this.speed+" "+this.size+" "+this.multi+" "+this.mutateChance+" "+(this.gen-1);
    }

    public void draw(Graphics2D g, int conX, int conY) {
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(colour[0], colour[1], colour[2]));
        g.fill(new Ellipse2D.Double(x+conX-size, y+conY-size, 2*size, 2*size));
        g.setColor(accCol);
        g.draw(new Line2D.Double(x+conX, y+conY, x+conX+size*Math.sin(Math.toRadians(this.angle-turnS/2)), y+conY+size*Math.cos(Math.toRadians(this.angle-turnS/2))));
        g.draw(new Line2D.Double(x+conX, y+conY, x+conX+size*Math.sin(Math.toRadians(this.angle+turnS/2)), y+conY+size*Math.cos(Math.toRadians(this.angle+turnS/2))));
        g.draw(new Ellipse2D.Double(x+conX-size, y+conY-size, 2*size, 2*size));
    }

    public void drawGhost(Graphics2D g, Color hlCol, double mX, double mY) {
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(colour[0], colour[1], colour[2]));
        g.fill(new Ellipse2D.Double(mX-size*1.5, mY-size*1.5, 2*size*1.5, 2*size*1.5));
        g.setColor(accCol);
        g.draw(new Line2D.Double(mX, mY, mX+size*1.5*Math.sin(Math.toRadians(180-turnS/2)), mY+size*1.5*Math.cos(Math.toRadians(180-turnS/2))));
        g.draw(new Line2D.Double(mX, mY, mX+size*1.5*Math.sin(Math.toRadians(180+turnS/2)), mY+size*1.5*Math.cos(Math.toRadians(180+turnS/2))));
        g.setColor(hlCol);
        g.draw(new Ellipse2D.Double(mX-size*1.5, mY-size*1.5, 2*size*1.5, 2*size*1.5));
    }
}
