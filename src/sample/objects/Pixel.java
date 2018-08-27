package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Pixel extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 15;

    public boolean isGravityWorking() {
        return isGravityWorking;
    }

    public void setGravityWorking(boolean gravityWorking) {
        isGravityWorking = gravityWorking;
    }

    private boolean isGravityWorking = true;

    public Pixel(Color color, int radius) {
        super(new Circle(1, 1, radius, color));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}