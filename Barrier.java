import javax.swing.*;

/**
 * Created by Rohil on 12/2/17.
 */
public class Barrier extends Sprite implements Constants {
    private final String barrierImg = "src/images/barrier.png";

    Barrier(int xPos, int yPos) {
        this.x = xPos;
        this.y = yPos;
        ImageIcon barrierImage = new ImageIcon(barrierImg);
        setImage(barrierImage.getImage());
    }

}
