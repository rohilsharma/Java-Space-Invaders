import com.sun.tools.internal.jxc.ap.Const;

import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class Defender extends Sprite implements Constants {

    private final int START_Y = BOARD_HEIGHT - 75; //280
    private final int START_X = BOARD_WIDTH / 2;

    private final String defenderImg = "src/images/defender.png";
    private int width;
    private int numDeaths = 0;

    public Defender() { initDefender(); }

    public void defenderDead() {
        numDeaths++;
    }

    public int getDeaths() {
        return numDeaths;
    }

    private void initDefender() {
        ImageIcon ii = new ImageIcon(defenderImg);
        width = ii.getImage().getWidth(null);
        setImage(ii.getImage());
        setX(START_X);
        setY(START_Y);
    }

    public void act() {
        x += dx;

        if (x <= 2) { x = 2; }
        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT) { dx = -2; }
        if(key == KeyEvent.VK_RIGHT) { dx = 2; }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) { dx = 0; }
        if (key == KeyEvent.VK_RIGHT) { dx = 0; }
    }
}