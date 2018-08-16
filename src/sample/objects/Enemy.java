package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy extends GameObject{
    private static final double MAX_GRAVITY_FACTOR = 5;
    public Enemy() {
        super(new Circle(15, 15, 15, Color.RED));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }

}
