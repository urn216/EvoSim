
/**
* Core class for the evolution simulator
*/

import java.io.*;
import java.nio.file.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import java.util.*;
// import java.awt.Color;
// import java.awt.Font;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Core extends JPanel
{
  static final long serialVersionUID = 1;

  private JFrame f;
  private boolean maximized = true;

  Display display;

  boolean run = false;
  boolean fastMode = false;
  boolean statMode = false;
  boolean autoMode = false;
  boolean locked = true;
  boolean quit = false;
  boolean controlling = false;

  List<Thing> oldThings = Collections.synchronizedList(new ArrayList<Thing>());
  List<Food> food = Collections.synchronizedList(new ArrayList<Food>());
  List<Double> thingSize = Collections.synchronizedList(new ArrayList<Double>());
  List<Double> foodSize = Collections.synchronizedList(new ArrayList<Double>());

  long countdown = 0;
  int count = 0;
  int fastCount = 0;

  public static int menuSize = 300;
  public static int buffer = 50;
  public static int infoSize = 400;
  public static int mapSize = 800;
  public static int maxFood = (int) mapSize/2;
  public static int toolBarY = 30;
  public static int toolBarX = 7;

  Button[] buttons;
  int numButtons = 11;
  String[] buttonNames = {"Play", "Fast Mode", "Show Stats", "Auto Focus", "Control Focus", "New Simulation", "Save Simulation", "Load Simulation", "End Simulation", "Full Screen", "Quit"};

  public static int screenSizeX = menuSize+buffer+mapSize+infoSize;
  public static int screenSizeY = buffer*2+mapSize;

  public static int conX = menuSize+buffer;
  public static int conY = buffer;

  public static void main(String[] args) {
    Core core = new Core();
    core.start();
    core.playGame();
  }

  public void start() {
    f = new JFrame("Evo Sim");
    f.getContentPane().add(this);
    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    f.setResizable(true);
    BufferedImage image = null;
    try {
      image = ImageIO.read(Core.class.getResourceAsStream("textures/icon.png"));
    }catch(IOException e){System.out.println("Canne doit");}
    f.setIconImage(image);
    f.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit = true;
      }
    });
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
          pause();
        }
        else if (!locked && e.getKeyCode() == KeyEvent.VK_F) {
          doFast();
        }
        else if (!locked && e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
          doStats();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          display.setFocus(null);
        }
        else if (!locked && e.getKeyCode() == KeyEvent.VK_O) {
          display.setFocus(oldThings.get(0));
        }
        else if (!locked && e.getKeyCode() == KeyEvent.VK_R) {
          int r = (int)(Math.random()*oldThings.size());
          display.setFocus(oldThings.get(r));
        }
        else if (e.getKeyCode() == KeyEvent.VK_F11) {
          doFull();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          doControl();
        }
      }
    });
    f.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        if (controlling && display.hasFocus()) {
          double x = e.getX();
          double y = e.getY();
          if (!maximized) {
            y -= toolBarY;
            x -= toolBarX;
          }
          Thing focus = display.getFocus();
          focus.setCont();
          if (x > conX && x < conX+mapSize && y > conY && y < conY+mapSize ) {
            focus.setTarget(x-conX, y-conY);
          }
          else {
            focus.setTarget(-1, -1);
          }
        }
      }
    });
    f.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        if (!maximized) {
          y -= toolBarY;
          x -= toolBarX;
        }
        if (x <= menuSize && e.getButton() == 1) {
          for (Button b : buttons) {
            if (b.touching(x, y)) {
              b.setIn();
            }
            else {
              b.setOut();
            }
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        if (!maximized) {
          y -= toolBarY;
          x -= toolBarX;
        }
        if (e.getButton() == 1) {
          if (x > menuSize) {
            for (Thing i : oldThings) {
              if (i.touching(x-conX, y-conY)) {
                display.setFocus(i);
              }
            }
          }
          else {
            for (Button b : buttons) {
              if (b.touching(x, y) && b.isIn()) {
                String name = b.getName();
                if (name.equals("Play") || name.equals("Resume") || name.equals("Pause")) {pause();}
                else if (!locked && name.equals("Fast Mode")) {doFast();}
                else if (!locked && (name.equals("Show Stats") || name.equals("Show Live"))) {doStats();}
                else if (!locked && (name.equals("Control Focus") || name.equals("Controlling"))) {doControl();}
                else if (!locked && (name.equals("Auto Focus") || name.equals("Auto: On") || name.equals("Auto: Off"))) {doAuto();}
                else if (name.equals("New Simulation")) {newGame();}
                else if (!locked && name.equals("Save Simulation")) {save();}
                else if (name.equals("Load Simulation")) {load();}
                else if (name.equals("End Simulation")) {endGame();}
                else if (name.equals("Full Screen")) {doFull();}
                else if (name.equals("Quit")) {quit = true;}
              }
            }
          }
          for (Button b : buttons) {
            b.setOut();
          }
        }
      }
    });
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.setUndecorated(true);
    f.setVisible(true);

    display = new Display();

    buttons = new Button[numButtons];

    for (int b = 0; b < numButtons; b++) {
      buttons[b] = new Button(buttonNames[b]);
      if ((b>0 && b<5) || b==6) {
        buttons[b].lock();
      }
    }

    //UI.addButton("Quit", UI::quit);
    //UI.setDivider(0.0);
    //UI.printMessage("Click on a creature to set it as your focus. Esc to deselect. Space to pause");
  }

  public void doFull() {
    f.removeNotify();
    if (maximized) {
      f.setExtendedState(JFrame.NORMAL);
      f.setUndecorated(false);
      f.addNotify();
      f.setSize(screenSizeX+toolBarX, screenSizeY+toolBarY);
    }
    else {
      f.setVisible(false);
      f.setExtendedState(JFrame.MAXIMIZED_BOTH);
      f.setUndecorated(true);
      f.setVisible(true);
      f.addNotify();
    }
    f.requestFocus();
    maximized = !maximized;
  }

  public void doFast() {
    fastMode = !fastMode;
  }

  public void doStats() {
    statMode = !statMode;
    if (statMode) {buttons[2].setName("Show Live");}
    else {buttons[2].setName("Show Stats");}
  }

  public void doAuto() {
    autoMode = !autoMode;
    if (autoMode) {buttons[3].setName("Auto: On");}
    else {buttons[3].setName("Auto: Off");}
  }

  public void doControl() {
    controlling = !controlling;
    if (controlling) {
      buttons[4].setName("Controlling");
      display.getFocus().setCont();
    }
    else {
      buttons[4].setName("Control Focus");
      display.getFocus().unsetCont();
    }
  }

  public void pause(){
    if (run) {
      buttons[0].setName("Resume");
      run = false;
    }
    else {
      buttons[0].setName("Pause");
      run = true;
      if (locked) {
        locked = false;
        buttons[1].unlock();
        buttons[2].unlock();
        buttons[3].unlock();
        buttons[4].unlock();
        buttons[6].unlock();
      }
    }
  }

  public void newGame() {
    count = 0;
    this.endGame();
    try {
      Thread.sleep(100);
    } catch(InterruptedException e){Thread.currentThread().interrupt();}
    run = true;
    buttons[0].setName("Pause");
    locked = false;
    buttons[1].unlock();
    buttons[2].unlock();
    buttons[3].unlock();
    buttons[4].unlock();
    buttons[6].unlock();
  }

  public void playGame() {
    while (true) {
      if (run) {
        if (count == 15) {
          double liveChance = Math.random();
          ArrayList<Thing> things = new ArrayList<Thing>();
          things.addAll(oldThings);
          //Create new food
          if (food.size() < maxFood) {
            for (int f = 0; f< (int) 1+Math.random()*8; f++) {
              double e = 15+124*Math.exp(-3.5*Math.random());
              double s = (e/10)+2;
              food.add(new Food(s+Math.random()*(mapSize-2*s), s+Math.random()*(mapSize-2*s), e));
            }
          }
          //Create new species
          if ((liveChance <= 1 && oldThings.size() < 8) || liveChance <=0.001) {
            things.add(new Thing((int) Math.round(Math.random()*255), (int) Math.round(Math.random()*255), (int) Math.round(Math.random()*255), 4+Math.random()*(mapSize-8), 4+Math.random()*(mapSize-8), 100, Math.random()*0.5, Math.random()*359, Math.random()*180, Math.random()*10, 2+Math.random()*14, Math.random()*4, Math.random(), -1));
            oldThings.clear();
            oldThings.addAll(things);
          }
          //Reproduction
          for (Thing i : things) {
            double replicateChance = Math.random();
            double[] stats = i.getStats();
            if (replicateChance <= stats[1] && stats[0] >= 50) {
              int[] col = i.getCol();
              oldThings.add(new Thing(col[0], col[1], col[2], stats[2], stats[3], stats[0]/2, stats[1], stats[4], stats[5], stats[6], stats[7], stats[8], stats[9], stats[10]));
              i.setEP(stats[0]/2);
            }
            if (stats[0] <= 0) {
              oldThings.remove(i);
              i = null;
            }
          }
          thingSize.add((double)oldThings.size());
          foodSize.add((double)food.size());

          if (foodSize.size() > mapSize) {
            foodSize.remove(0);
            thingSize.remove(0);
          }
          count = 0;
        }
        for (int i = 0; i < oldThings.size(); i++) {
          double[] stats = oldThings.get(i).getStats();
          ArrayList<Food> newFood = new ArrayList<Food>();
          newFood.addAll(food);
          if (((int) stats[8]) == 1 || ((int) stats[8]) == 3) {
            for (Food f : newFood) {
              double [] fStats = f.getStats();
              if (oldThings.get(i).touching(fStats[1], fStats[2]) && stats[7] > fStats[0]/10) {
                if (fStats[0] > 10) {
                  oldThings.get(i).setEP(stats[0]+10);
                  f.setEP(fStats[0]-10);
                }
                else {
                  oldThings.get(i).setEP(stats[0]+fStats[0]);
                  food.remove(f);
                  f = null;
                }
                stats = oldThings.get(i).getStats();
              }
            }
          }
          if (((int) stats[8]) == 2 || ((int) stats[8]) == 3) {
            for (int j = 0; j < oldThings.size(); j++) {
              double [] fStats = oldThings.get(j).getStats();
              if (oldThings.get(i).touching(fStats[2], fStats[3]) && stats[7] > 1.2*fStats[7]) {
                oldThings.get(i).setEP(stats[0]+(1.5*fStats[0]));
                stats = oldThings.get(i).getStats();
                oldThings.get(j).setEP(0);
                oldThings.remove(j);
                if (i >= j) {
                  i -= 1;
                }
                j -= 1;
              }
            }
          }
          oldThings.get(i).move();
          oldThings.get(i).setEP(stats[0]-0.001*(stats[7]/2)*(stats[7]/2)*(stats[6]+(stats[5]/10)+(((int) stats[8] == 3) ? 5 : 0)));
        }
        count ++;
        countdown++;
      }
      fastCount++;
      repaint();
      if (quit) {
        System.exit(0);
      }
      try {
        if (!fastMode) {
          Thread.sleep(33);
        } else {Thread.sleep(3);};
      } catch(InterruptedException e){Thread.currentThread().interrupt();}
    }
  }

  public void endGame() {
    run = false;
    try {
      Thread.sleep(100);
    } catch(InterruptedException e){Thread.currentThread().interrupt();}
    repaint();
    fastCount = 0;
    fastMode = false;
    statMode = false;
    autoMode = false;
    locked = true;
    buttons[1].lock();
    buttons[2].lock();
    buttons[3].lock();
    buttons[4].lock();
    buttons[6].lock();
    buttons[2].setName("Show Stats");
    buttons[0].setName("Play");
    buttons[3].setName("Auto Focus");
    buttons[4].setName("Control Focus");
    oldThings.clear();
    food.clear();
    thingSize.clear();
    foodSize.clear();
    // int gMax = 0;
    display.setFocus(null);
  }

  public void paint(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;
    if (fastMode && fastCount < 11) {
      return;
    }
    else {
      //List<Thing> drawThings = new ArrayList<Thing>();
      //drawThings.addAll(oldThings);
      //List<Food> drawFood = new ArrayList<Food>();
      //drawFood.addAll(food);
      display.redraw(g, buttons, food, oldThings, foodSize, thingSize, countdown, autoMode, statMode, fastMode);
      fastCount = 0;
    }
  }

  public String getFile(boolean isSave, String title) {
    JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    j.setDialogTitle(title);
    int returnValue = isSave ? j.showSaveDialog(null) : j.showOpenDialog(null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      return j.getSelectedFile().getAbsolutePath();
    }
    else {
      return null;
    }
  }

  public void save() {
    run=false;
    try {
      String filename = getFile(true, "Save");
      if (filename==null) {return;}
      if (!filename.endsWith(".txt") && !filename.endsWith(".TXT")) {
        filename = filename+".txt";
      }
      PrintStream out = new PrintStream(filename);
      out.println(filename);
      out.println("Map Size: " + mapSize);
      out.println();
      for (Thing i : this.oldThings) {
        out.println(i);
      }
      for (Food f : this.food) {
        out.println(f);
      }
      out.close();
      this.pause();
    } catch(IOException e){System.err.println("Saving failed" + e);}
  }

  public void load() {
    run = false;
    String filename = getFile(false, "Save File to Load");
    if (filename==null || (!filename.endsWith(".txt") && !filename.endsWith(".TXT"))) {return;}
    try {
      count = 0;
      this.endGame();
      Thread.sleep(100);
      List<String> allLines = Files.readAllLines(Paths.get(filename));
      for (String line : allLines) {
        Scanner scan = new Scanner(line);
        String type;
        if (scan.hasNext()) {
          type = scan.next();
        }
        else {type = "gap";}
        if (type.equals("Thing")) {
          oldThings.add(new Thing(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));
        }
        else if (type.equals("Food")) {
          food.add(new Food(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));
        }
        scan.close();
      }
      this.pause();
    } catch(IOException e){System.err.println("Loading failed" + e);}
    catch(InterruptedException e){System.err.println("Sleeping Failed" + e);}
  }
}
