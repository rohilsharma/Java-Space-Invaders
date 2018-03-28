import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.sound.sampled.*;
import java.io.File;

public class Platform extends JPanel implements Runnable, Constants {
    private Dimension d;
    private Invader[][] invaderArray = new Invader[INVADER_ROWS+1][INVADER_COLUMNS];
    private Defender defender;
    private Shot shot;
    private Barrier[][] barrier1 = new Barrier[4][4];
    private Barrier[][] barrier2 = new Barrier[4][4];
    private Barrier[][] barrier3 = new Barrier[4][4];

    private final int ALIEN_INIT_X = 150;
    private final int ALIEN_INIT_Y = 5;
    private int direction = -1;
    private int kills = 0;

    private boolean keepPlaying = true;
    private final String explImg = "src/images/explosion.png";
    private String message = "Game Over!";

    private Thread animator;

    String backgroundMusic = "src/sounds/background_music1.wav";
    Clip backgroundClip;

    public Platform() { initPlatform(); }

    private void initPlatform() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.BLACK);

        gameInit();
        setDoubleBuffered(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        gameInit();
    }

    public void gameInit() {
        for (int i = 0; i < INVADER_ROWS+1; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                if(i == INVADER_ROWS) {
                    invaderArray[i][j] = new Invader(ALIEN_INIT_X + 18 * j, ALIEN_INIT_Y + 18 * i);
                    invaderArray[i][j].setDying(true);
                    invaderArray[i][j].setVisible(false);
                } else {
                    invaderArray[i][j] = new Invader(ALIEN_INIT_X + 18 * j, ALIEN_INIT_Y + 18 * i);
                }
            }
        }

        defender = new Defender();
        shot = new Shot();

        //first barrier
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(i == 0) { //first row
                    barrier1[i][j] = new Barrier((10*j + 71), Constants.BOARD_HEIGHT - 175);
                } else if (i == 1) { //second row
                    barrier1[i][j] = new Barrier((10*j + 71), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else if (i == 2) { //third row
                    barrier1[i][j] = new Barrier((10*j + 71), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else { //fourth row
                    barrier1[i][j] = new Barrier((10*j + 71), Constants.BOARD_HEIGHT - 175 + 10*i);
                }
             }
        }

        //second barrier
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(i == 0) { //first row
                    barrier2[i][j] = new Barrier((10*j + 214), Constants.BOARD_HEIGHT - 175);
                } else if (i == 1) { //second row
                    barrier2[i][j] = new Barrier((10*j + 214), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else if (i == 2) { //third row
                    barrier2[i][j] = new Barrier((10*j + 214), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else { //fourth row
                    barrier2[i][j] = new Barrier((10*j + 214), Constants.BOARD_HEIGHT - 175 + 10*i);
                }
            }
        }

        //third barrier
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(i == 0) { //first row
                    barrier3[i][j] = new Barrier((10*j + 357), Constants.BOARD_HEIGHT - 175);
                } else if (i == 1) { //second row
                    barrier3[i][j] = new Barrier((10*j + 357), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else if (i == 2) { //third row
                    barrier3[i][j] = new Barrier((10*j + 357), Constants.BOARD_HEIGHT - 175 + 10*i);
                } else { //fourth row
                    barrier3[i][j] = new Barrier((10*j + 357), Constants.BOARD_HEIGHT - 175 + 10*i);
                }
            }
        }

        if (animator == null || !keepPlaying) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void drawInvaders(Graphics g) {
        for (int i = 0; i < INVADER_ROWS+1; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                if(invaderArray[i][j].isVisible()) { g.drawImage(invaderArray[i][j].getImage(), invaderArray[i][j].getX(), invaderArray[i][j].getY(), this); }
                if(invaderArray[i][j].isDying()) { invaderArray[i][j].die(); }
            }
        }
    }

    public void drawDefender(Graphics g) {
        if (defender.isVisible()) {
            g.drawImage(defender.getImage(), defender.getX(), defender.getY(), this);
        }
        if(defender.getDeaths() == NUM_LIVES) {
            defender.die();
            keepPlaying = false;
        }
    }

    public void drawShot(Graphics g) {
        if (shot.isVisible()) {
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);

            if ((Math.abs(shot.getX() - defender.getX()) <= 10) && (Math.abs(shot.getY() - defender.getY()) <= 10)) {
                try {
                    String soundName = "src/sounds/defenderShot.wav";
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (UnsupportedAudioFileException ex1) {
                    ex1.printStackTrace();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                } catch (LineUnavailableException ex3) {
                    ex3.printStackTrace();
                }
            }
        }
    }

    public void drawMissile(Graphics g) {
        for (int i = 0; i < INVADER_ROWS; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                Invader.Missile m = invaderArray[i][j].getMissile();
                if (!m.isDestroyed()) {
                    g.drawImage(m.getImage(), m.getX(), m.getY(), this);

                    if ((Math.abs(m.getX() - invaderArray[i][j].getX()) <= 10) && (Math.abs(m.getY() - invaderArray[i][j].getY()) <= 10)) {
                        try {
                            String soundName = "src/sounds/invaderMissile.wav";
                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioInputStream);
                            clip.start();
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                            }
                            clip.stop();
                        } catch (UnsupportedAudioFileException ex1) {
                        } catch (IOException ex2) {
                        } catch (LineUnavailableException ex3) {
                        }

                    }
                }
            }
        }
    }

    public void drawBarrier(Graphics g) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(barrier1[i][j].isVisible()) { g.drawImage(barrier1[i][j].getImage(), barrier1[i][j].getX(), barrier1[i][j].getY(), this); }
                if(barrier1[i][j].isDying()) { barrier1[i][j].die(); }

                if(barrier2[i][j].isVisible()) { g.drawImage(barrier2[i][j].getImage(), barrier2[i][j].getX(), barrier2[i][j].getY(), this); }
                if(barrier2[i][j].isDying()) { barrier2[i][j].die(); }

                if(barrier3[i][j].isVisible()) { g.drawImage(barrier3[i][j].getImage(), barrier3[i][j].getX(), barrier3[i][j].getY(), this); }
                if(barrier3[i][j].isDying()) { barrier3[i][j].die(); }
            }
        }
    }

    public void drawScore(Graphics g) {
        g.drawString("Score: " + Integer.toString(kills), 350, 467);
    }

    public void drawLives(Graphics g) {
        g.drawString("Lives Remaining: " + Integer.toString(NUM_LIVES - defender.getDeaths()), 100, 467);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#00004c"));
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (keepPlaying) {
            //g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawInvaders(g);
            drawDefender(g);
            drawShot(g);
            drawMissile(g);
            drawBarrier(g);
            drawScore(g);
            drawLives(g);
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void animationCycle() {
        if (kills == INVADER_ROWS * INVADER_COLUMNS) {
            keepPlaying = false;
            message = "Game won!";
            return;
        }

        // defender
        defender.act();

        // shot
        if (shot.isVisible()) {
            int shotX = shot.getX();
            int shotY = shot.getY();

            for (int i = 0; i < INVADER_ROWS; i++) {
                for (int j = 0; j < INVADER_COLUMNS; j++) {
                    int invaderX = invaderArray[i][j].getX();
                    int invaderY = invaderArray[i][j].getY();
                    if (invaderArray[i][j].isVisible() && shot.isVisible()) {
                        if (shotX >= (invaderX) && shotX <= (invaderX + INVADER_WIDTH) && shotY >= (invaderY) && shotY <= (invaderY + INVADER_HEIGHT)) {
                            ImageIcon ii = new ImageIcon(explImg);
                            invaderArray[i][j].setImage(ii.getImage());
                            invaderArray[i][j].setDying(true);
                            kills++;
                            shot.die();
                        }
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        // invaders

        for (int i = 0; i < INVADER_ROWS; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                int x = invaderArray[i][j].getX();
                if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                    direction = -1;

                    for (int k = 0; k < INVADER_ROWS; k++) {
                        for (int l = 0; l < INVADER_COLUMNS; l++) {
                            Invader a2 = invaderArray[k][l];
                            a2.setY(a2.getY() + GO_DOWN);
                            invaderArray[k][l].setShoot(true);
                        }
                    }
                    invaderArray[i][j].setShoot(true);
                }

                if (x <= BORDER_LEFT && direction != 1) {
                    direction = 1;

                    for (int o = 0; o < INVADER_ROWS; o++) {
                        for (int p = 0; p < INVADER_COLUMNS; p++) {
                            Invader a2 = invaderArray[o][p];
                            a2.setY(a2.getY() + GO_DOWN);
                            invaderArray[o][p].setShoot(true);
                        }
                    }
                    invaderArray[i][j].setShoot(true);
                }

                if(defender.getX() == invaderArray[i][j].getX() && (Math.abs(defender.getY() - invaderArray[i][j].getY()) <= 10)) {
                    defender.defenderDead();
                }

            }
        }

        for (int i = 0; i < INVADER_ROWS; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                if (invaderArray[i][j].isVisible()) {
                    int y = invaderArray[i][j].getY();
                    if (y > GROUND - INVADER_HEIGHT) {
                        keepPlaying = false;
                        message = "You have lost!";
                    }
                    invaderArray[i][j].act(direction);
                }
            }
        }

        // missiles
        Random gen = new Random();

        for (int i = 0; i < INVADER_ROWS; i++) {
            for (int j = 0; j < INVADER_COLUMNS; j++) {
                int shotChance = gen.nextInt((11-CHANCE)*2);
                Invader.Missile m = invaderArray[i][j].getMissile();
                if (shotChance <= 2 && invaderArray[i][j].isVisible() && m.isDestroyed() && invaderArray[i][j].canShoot()) {
                    try {
                        if(invaderArray[i+1][j].isDying()) {
                            m.setDestroyed(false);
                            m.setX(invaderArray[i][j].getX());
                            m.setY(invaderArray[i][j].getY());
                            invaderArray[i][j].setShoot(false);
                        }
                    } catch (IndexOutOfBoundsException e) { }
                }

                int missileX = m.getX();
                int missileY = m.getY();
                int defenderX = defender.getX();
                int defenderY = defender.getY();

                if(defender.getDeaths() < NUM_LIVES) {
                    String defenderImg = "src/images/defender.png";
                    ImageIcon ii = new ImageIcon(defenderImg);
                    defender.setImage(ii.getImage());
                }

                if (defender.isVisible() && !m.isDestroyed()) {
                    if (missileX >= (defenderX) && missileX <= (defenderX + PLAYER_WIDTH) && missileY >= (defenderY) && missileY <= (defenderY + PLAYER_HEIGHT)) {
                        m.setDestroyed(true);
                        ImageIcon ii = new ImageIcon(explImg);
                        defender.setImage(ii.getImage());
                        defender.defenderDead();
                    }
                }

                if (!m.isDestroyed()) {
                    m.setY(m.getY() + 1);
                    if (m.getY() >= GROUND - BOMB_HEIGHT) {
                        m.setDestroyed(true);
                    }
                }

                if((Math.abs(m.getX() - shot.getX()) <= 5) && (Math.abs(m.getY() - shot.getY())) <= 5) {
                    m.setDestroyed(true);
                    shot.setDying(true);
                }
            }
        }

        //barriers

        for (int q = 0; q < INVADER_ROWS; q++) {
            for (int w = 0; w < INVADER_COLUMNS; w++) {
                Invader.Missile missile = invaderArray[q][w].getMissile();

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if((!barrier1[i][j].isDying()) && (!missile.isDestroyed()) && (Math.abs(missile.getX() - barrier1[i][j].getX()) <= 3) && (Math.abs((missile.getY() - barrier1[i][j].getY())) <= 3)) {
                            ImageIcon ii = new ImageIcon(explImg);
                            barrier1[i][j].setImage(ii.getImage());
                            barrier1[i][j].setDying(true);
                            missile.setDestroyed(true);
                        }
                        if((!barrier2[i][j].isDying()) && (!missile.isDestroyed()) && (Math.abs(missile.getX() - barrier2[i][j].getX()) <= 3) && (Math.abs((missile.getY() - barrier2[i][j].getY())) <= 3)) {
                            ImageIcon ii = new ImageIcon(explImg);
                            barrier2[i][j].setImage(ii.getImage());
                            barrier2[i][j].setDying(true);
                            missile.setDestroyed(true);
                        }
                        if((!barrier2[i][j].isDying()) && (!missile.isDestroyed()) && (Math.abs(missile.getX() - barrier3[i][j].getX()) <= 3) && (Math.abs((missile.getY() - barrier3[i][j].getY())) <= 3)) {
                            ImageIcon ii = new ImageIcon(explImg);
                            barrier3[i][j].setImage(ii.getImage());
                            barrier3[i][j].setDying(true);
                            missile.setDestroyed(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        //ADD BACKGROUND MUSIC HERE
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(backgroundMusic).getAbsoluteFile());
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException ex1) {
        } catch (IOException ex2) {
        } catch (LineUnavailableException ex3) {
        }


        while (keepPlaying) {
            repaint();
            animationCycle();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) { sleep = 2; }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            beforeTime = System.currentTimeMillis();
        }
        endGame();
    }

    public void endGame() {
        if(backgroundClip.isRunning()) {
            backgroundClip.stop();
        }

        if(!backgroundClip.isRunning()) {
            backgroundClip.close();
        }

        Graphics g = this.getGraphics();

        g.setColor(Color.decode("#00004c"));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message)) / 2, BOARD_WIDTH / 2);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) { defender.keyReleased(e); }

        @Override
        public void keyPressed(KeyEvent e) {
            defender.keyPressed(e);

            int x = defender.getX();
            int y = defender.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if (keepPlaying) {
                    //shot = new Shot(x, y);
                    if (!shot.isVisible()) { shot = new Shot(x, y); }
                }
            }
        }
    }
}