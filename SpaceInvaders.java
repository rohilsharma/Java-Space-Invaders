/**
 * Created by Rohil on 12/1/17.
 */

import java.awt.*;
import javax.swing.JFrame;

public class SpaceInvaders extends JFrame implements Constants {

    public SpaceInvaders() {
        startUserInterface();
    }

    private void startUserInterface() {
        add(new Platform());
        setTitle("Space Invaders by Rohil Sharma");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SpaceInvaders ex = new SpaceInvaders();
            ex.setVisible(true);
        });
    }
}