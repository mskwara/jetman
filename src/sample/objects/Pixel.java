package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


class Pixel extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 15;
    private static final int PIXEL_RADIUS = 2;

    Pixel(Color color) {
        super(new Circle(1, 1, PIXEL_RADIUS, color));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }
}