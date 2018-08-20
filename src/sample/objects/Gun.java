package sample.objects;

import java.util.Collections;
import java.util.List;

public class Gun implements Weapon{
    public static final int BULLET_SPEED_FACTOR = 15;

    @Override
    public List<GameObject> fire() {
        return Collections.singletonList(new Bullet());
    }
}
