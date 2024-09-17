package Dino_Game_java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

class Game extends Frame {
    final int D_W = 1200;
    final int D_H = 550;
    static int unit = 10;
    Color colorDinosaur = Color.GRAY;
    Color colorGameOver1 = Color.black;
    Color colorGameOver2 = Color.yellow;
    Color colorCactus1 = Color.gray;
    Color colorCactus2 = Color.gray;
    int jump = 0;
    int jumpY = 0;
    boolean onEnterPresses = false;
    boolean down = false;
    List<MyGraph> myGraphs = new ArrayList<>();
    int currentDinosaurX = 0;
    int currentDinosaurY = 0;
    boolean gameOver = false;
    DrawPanel drawPanel = new DrawPanel();

    public static void main(String args[]) {
        new Game();
    }

    public Game() {
        super("Run Dino Run");
        setSize(D_W, D_H); // Set the size of the window
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        initCactusG();

        ActionListener gameTimerListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    if (jump >= D_W) {
                        jump = 0;
                        initCactusG();
                    } else {
                        jump += 10;
                    }
                    drawPanel.repaint();
                }
            }
        };

        Timer gameTimer = new Timer(40, gameTimerListener);
        gameTimer.start();

        ActionListener jumpTimerListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    if (onEnterPresses) {
                        if (down) {
                            jumpY -= 20;
                        } else {
                            jumpY += 20;
                        }
                    }
                    if (jumpY >= 280) {
                        down = true;
                    }
                    if (jumpY <= 0) {
                        onEnterPresses = false;
                        down = false;
                        jumpY = 0;
                    }
                    drawPanel.repaint();
                }
            }
        };

        Timer jumpTimer = new Timer(80, jumpTimerListener);
        jumpTimer.start();

        add(drawPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initCactusG() {
        Random rr = new Random();
        int nbr = 2;
        int x_ = 10;
        int y_ = 100;
        int h_ = 60;
        int p_ = 10;
        myGraphs = new ArrayList<MyGraph>();
        for (int it = 0; it < nbr; it++) {
            Random r = new Random();
            int step = r.nextInt(10) + 1;
            MyGraph myGraph = new MyGraph();
            myGraph.x_ = x_ * 30 + step * 10 + 600;
            myGraph.h_ = 10 + (6 * step) + 2;
            myGraph.y_ = 300 - h_;
            myGraph.p_ = 8 + step / 2;
            myGraphs.add(myGraph);
        }
    }

    private void drawCactus(Graphics g) {
        for (MyGraph myGraph : myGraphs) {
            int x = myGraph.x_;
            int h = myGraph.h_;
            int y = myGraph.y_;
            int p = myGraph.p_;
            if (x - jump < -100) {
                // Reset cactus when it moves out of screen
                myGraph.x_ = D_W + new Random().nextInt(400);
            }
            draw(g, x - jump, y, h, p);
        }
    }

    private void gameOver(Graphics g) {
        Graphics2D graph = (Graphics2D) g;
        graph.setPaint(colorGameOver1);
        graph.setFont(new Font("MV Boli", Font.BOLD, 50));
        graph.drawString("Game Over", 550, 150);

        graph.setFont(new Font("MV Boli", Font.BOLD, 30));
        graph.drawString("Press Space key to restart!!", 350, 250);
    }

    private void restartGame() {
        gameOver = false;
        jump = 0;
        jumpY = 0;
        onEnterPresses = false;
        down = false;
        initCactusG();
    }

    private void drawSun(Graphics g) {
        Graphics2D sun1 = (Graphics2D) g;
        sun1.setPaint(new Color(255, 255, 0));
        sun1.fillArc(900, 70, 80, 80, 90, 180);
    }

    private void drawSol(Graphics g) {
        Graphics2D sol = (Graphics2D) g;
        sol.setPaint(Color.orange);
        sol.fillRect(0, D_H - 100, D_W, 100);
    }

    private void drawDinausor(Graphics g) {
        int xDinausor = 180;
        g.setColor(colorDinosaur);
        currentDinosaurX = xDinausor;
        currentDinosaurY = D_H - 150 - jumpY;
        drawRaw(g, xDinausor, currentDinosaurY, 10, 15);
    }

    private void drawRaw(Graphics g, int Dinausor, int y, int w, int h) {
        Graphics2D sun16 = (Graphics2D) g;
        sun16.fillRect(Dinausor, y, w * unit, h * unit);
    }

    private void draw(Graphics g, int x, int y, int h, int p) {
        if (x <= currentDinosaurX && x + p >= currentDinosaurX && y <= currentDinosaurY) {
            gameOver = true;
            return;
        }
        g.setColor(colorCactus1);
        g.fillRect(x, y, p, h);
    }

    private class DrawPanel extends JPanel {
        public DrawPanel() {
            MoveAction action = new MoveAction("onEnter");
            String ACTION_KEY = "onEnter";
            KeyStroke W = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(W, ACTION_KEY);
            ActionMap actionMap = getActionMap();
            actionMap.put(ACTION_KEY, action);

            // Key listener for space bar to restart the game
            addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
                        restartGame();
                    }
                }
            });
            setFocusable(true);  // Required to receive key events
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawCactus(g);
            drawSun(g);
            drawSol(g);
            drawDinausor(g);
            if (gameOver) {
                gameOver(g);
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(D_W, D_H);
        }
    }

    private class MyGraph {
        int x_, y_, h_, p_;
    }

    class MoveAction extends AbstractAction {
        public MoveAction(String name) {
            putValue(NAME, name);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            onEnterPresses = true;
            drawPanel.repaint();
        }
    }
}
