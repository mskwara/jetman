package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 15;

    public Bullet(){
        super(new Circle(5, 5, 5, Color.BROWN));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}