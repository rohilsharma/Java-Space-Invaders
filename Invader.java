import javax.swing.ImageIcon;

public class Invader extends Sprite {
    private Missile missile;
    private final String invaderImg = "src/images/invader.png";
    private boolean hasShot = true;

    public Invader(int x, int y) { initInvader(x, y); }

    private void initInvader(int x, int y) {
        this.x = x;
        this.y = y;

        missile = new Missile(x, y);
        ImageIcon ii = new ImageIcon(invaderImg);
        setImage(ii.getImage());
    }

    public boolean canShoot() { return this.hasShot; }
    public void setShoot(boolean shoot) { this.hasShot = shoot; }

    public void act(int direction) { this.x += direction; }

    public Missile getMissile() { return missile; }

    public class Missile extends Sprite {
        private final String missileImg = "src/images/missile.png";
        private boolean destroyed;

        public Missile(int x, int y) { initMissile(x, y); }

        private void initMissile(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(missileImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
        public boolean isDestroyed() { return destroyed; }
    }
}