package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 15;
    private static final int BULLET_RADIUS = 5;

    public Bullet() {
        super(new Circle(5, 5, BULLET_RADIUS, Color.BROWN));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}