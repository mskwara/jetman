package sample.objects;

import java.util.Collections;
import java.util.List;

public class Gun implements Weapon{
    static final int BULLET_SPEED_FACTOR = 15;
    static final int BULLETS_SIZE = 9;
    static final int DAMAGES = 15;
    private double bulletsNumber = BULLETS_SIZE;

    public int getBulletsNumber() {
        return (int) bulletsNumber;
    }

    public void setBulletsNumber(int bulletsNumber) {
        this.bulletsNumber = bulletsNumber;
    }

    public void changeBulletsNumber(double amount) {
        this.bulletsNumber = this.bulletsNumber <= BULLETS_SIZE ? this.bulletsNumber + amount : BULLETS_SIZE;
    }

    @Override
    public List<GameObject> fire() {
        if (bulletsNumber >= 1) {
            bulletsNumber--;
            return Collections.singletonList(new Bullet());
        }
        return Collections.emptyList();
    }
}
