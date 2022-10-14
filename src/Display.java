
/**
 * Display class for the evolution simulator
 */

//import ecs100.*;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
// import java.io.*;
// import java.nio.file.*;
// import javax.swing.JButton;

import java.awt.Graphics2D;
import java.awt.BasicStroke;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Display
{
    static int mapSize = Core.mapSize;
    static int menuSize = Core.menuSize;

    static int screenSizeX = Core.screenSizeX;
    static int screenSizeY = Core.screenSizeY;

    static int conX = Core.conX;
    static int conY = Core.conY;

    int buttonBuffer = 20;
    int buttonWidth = menuSize-buttonBuffer*2;
    int buttonHeight = 50;

    Color bgCol = new Color(220, 220, 220);
    Color accCol = new Color(80, 80, 80);
    Color mapCol = new Color(200, 200, 220);
    Color menuCol = new Color(180, 180, 180);
    Color hlCol = new Color(255, 165, 0);

    int gMax;
    long nextdown;

    Thing focus = null;

    public void setFocus(Thing f) {
        focus = f;
    }

    public Thing getFocus() {
        return focus;
    }

    public boolean hasFocus() {
        if (focus != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void plot(Graphics2D g) {
        g.setColor(bgCol);
        g.fillRect(menuSize, 0, screenSizeX-menuSize, screenSizeY);
        g.setColor(mapCol);
        g.fillRect(conX, conY, mapSize, mapSize);
        g.setColor(accCol);
        g.setStroke(new BasicStroke(2));
        g.drawRect(conX, conY, mapSize, mapSize);
        g.setStroke(new BasicStroke(1));
    }

    public void menu(Graphics2D g, Button[] buttons) {
        g.setColor(menuCol);
        g.fill(new Rectangle2D.Double(0, 0, menuSize, screenSizeY));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].draw(g, buttonBuffer, buttonBuffer+(buttonHeight+buttonBuffer)*i, buttonWidth, buttonHeight, mapCol, accCol, hlCol, bgCol);
        }
    }

    public void redraw(Graphics2D g, Button[] buttons, List<Food> food, List<Thing> oldThings, List<Double> foodSize, List<Double> thingSize, long countdown, boolean autoMode, boolean statMode, boolean fastMode) {
        this.plot(g);
        this.menu(g, buttons);
        if (oldThings.size() > gMax) {gMax = oldThings.size();}
        if (food.size() > gMax) {gMax = food.size();}
        if (!fastMode && !statMode) {
            for (Food f : food) {
                f.draw(g, conX, conY);
            }
            for (Thing i : oldThings) {
                i.draw(g, conX, conY);
            }
        }
        if (statMode || fastMode) {
            g.setFont(new Font("Copperplate", Font.PLAIN, 12));
            g.drawString("Pop: " + (int) Math.round(thingSize.get(0)), menuSize+5, (int) (conY+mapSize-((thingSize.get(0)/gMax)*mapSize)) );
            g.drawString("Food: " + (int) Math.round(foodSize.get(0)), menuSize, (int) (conY+mapSize-((foodSize.get(0)/gMax)*mapSize)) );
            g.setStroke(new BasicStroke(2));
            for (int i = 1; i < foodSize.size(); i ++) {
                double fT = thingSize.get(i-1);
                double fF = foodSize.get(i-1);
                double lT = thingSize.get(i);
                double lF = foodSize.get(i);
                g.setColor(new Color(50, 0, 255));
                g.draw(new Line2D.Double(conX+i-1, conY+mapSize-((fT/gMax)*mapSize), conX+i, conY+mapSize-((lT/gMax)*mapSize)) );
                g.setColor(new Color(50, 160, 0));
                g.draw(new Line2D.Double(conX+i-1, conY+mapSize-((fF/gMax)*mapSize), conX+i, conY+mapSize-((lF/gMax)*mapSize)) );
            }
            g.setStroke(new BasicStroke(1));
        }
        g.setColor(accCol);
        g.setFont(new Font("Copperplate", Font.PLAIN, 50));
        g.drawString("Population: " + oldThings.size(), conX+mapSize+10, 100);
        g.drawString("Food Total: " + food.size(), conX+mapSize+10, 200);
        if (focus != null) {
            this.focusText(g, oldThings, countdown, autoMode, statMode, fastMode);
        }
    }

    public void focusText(Graphics2D g, List<Thing> oldThings, long countdown, boolean autoMode, boolean statMode, boolean fastMode) {
        //UI.setColor(Color.white);
        //UI.fillRect(mapSize+55, 205, 400, 400);
        double[] stats = focus.getStats();
        if (stats[0] <= 0) {
            stats[0] = 0;
            if (countdown >= nextdown) {
                if (autoMode) {
                    int r = (int)(Math.random()*oldThings.size());
                    focus = oldThings.get(r);
                }
                else {
                    focus = null;
                    return;
                }
            }
        }
        else {
            nextdown = countdown + 120;
        }
        String diet;
        if ((int) stats[8] == 0) {
            diet = "None";
        } else if ((int) stats[8] == 1) {
            diet = "Herbivore";
        } else if ((int) stats[8] == 2) {
            diet = "Carnivore";
        } else {
            diet = "Omnivore";
        }
        focus.drawGhost(g, hlCol, conX+mapSize+250, 260);
        //UI.setColor(Color.orange);
        if (!fastMode && !statMode) {
            g.draw(new Ellipse2D.Double(stats[2]+conX-stats[7], stats[3]+conY-stats[7], 2*stats[7], 2*stats[7]));
        }
        g.setFont(new Font("Copperplate", Font.PLAIN, 20));
        g.drawString("Energy: " + String.format("%.2f",stats[0]), conX+mapSize+10, 250);
        g.drawString("Generation: " + String.format("%.0f",stats[10]), conX+mapSize+10, 270);
        g.drawString("Max Speed: " + String.format("%.2f",stats[6]) + "p/s", conX+mapSize+10, 290);
        g.drawString("Reproduction chance: " + String.format("%.2f",100*stats[1]) + "%", conX+mapSize+10, 310);
        g.drawString("Mutation chance: " + String.format("%.2f",100*stats[9]) + "%", conX+mapSize+10, 330);
        g.drawString("Diet: " + diet, conX+mapSize+10, 350);
    }
}
