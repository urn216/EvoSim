
import java.awt.Color;
import java.awt.Font;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Class for making functional Buttons
 */
public class Button
{

    String name;
    boolean in = false;
    boolean locked = false;
    
    double x = 0;
    double y = 0;
    double width = 0;
    double height = 0;

    /**
     * Constructor for Buttons
     */
    public Button(String name)
    {
        this.name = name;
    }

    public String setName(String name) {
        this.name = name;
        return this.name;
    }

    public String getName() {
        return name;
    }
    
    public void setIn() {
        in = true;
    }
    
    public void setOut() {
        in = false;
    }
    
    public void lock() {
        locked = true;
    }
    
    public void unlock() {
        locked = false;
    }
    
    public boolean isIn() {
        return in;
    }
    
    public boolean touching(double oX, double oY) {
        if (oX > x && oX < x+width && oY > y && oY < y+height) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public void draw(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol, Color inCol, Color lockCol) {
        if (locked) {
            drawLocked(g, x, y, width, height, lockCol, accCol);
        }
        else if (in) {
            drawIn(g, x, y, width, height, accCol, inCol);
        }
        else {
            drawOut(g, x, y, width, height, outCol, accCol);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawOut(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol) {
        g.setColor(outCol);
        g.fill(new Rectangle2D.Double(x, y, width, height));
        g.setColor(accCol);
        g.draw(new Rectangle2D.Double(x, y, width, height));
        g.setFont(new Font("Copperplate", Font.BOLD, 24));
        g.drawString(name, x+width/2-(name.length()*12)/2, y+(height+14)/2);
    }
    
    public void drawIn(Graphics2D g, float x, float y, float width, float height, Color inCol, Color accCol) {
        g.setColor(inCol);
        g.fill(new Rectangle2D.Double(x, y, width-2, height-2));
        g.setColor(accCol);
        g.draw(new Rectangle2D.Double(x, y, width, height));
        g.setFont(new Font("Copperplate", Font.BOLD, 24));
        g.drawString(name, x+2+width/2-(name.length()*12)/2, y+2+(height+14)/2);
    }
    
    public void drawLocked(Graphics2D g, float x, float y, float width, float height, Color lockCol, Color accCol) {
        g.setColor(lockCol);
        g.fill(new Rectangle2D.Double(x, y, width, height));
        g.setColor(accCol);
        g.draw(new Rectangle2D.Double(x, y, width, height));
        g.setFont(new Font("Copperplate", Font.BOLD, 24));
        g.drawString(name, x+width/2-(name.length()*12)/2, y+(height+14)/2);
    }
}
