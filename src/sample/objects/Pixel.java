package sample.objects;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Pixel extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 15;
    static final int PIXEL_RADIUS = 2;

    public Pixel(Color color){
        super(new Circle(1, 1, PIXEL_RADIUS, color));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}