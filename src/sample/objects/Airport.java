package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Airport extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 0;

    public Airport() {
        super(new Rectangle(150, 10, Color.GRAY));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}
