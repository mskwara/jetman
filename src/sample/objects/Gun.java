package sample.objects;

import java.util.Collections;
import java.util.List;

public class Gun implements Weapon{
    public static final int BULLET_SPEED_FACTOR = 10;

    @Override
    public List<GameObject> fire() {
        return Collections.singletonList(new Bullet());
    }
}
