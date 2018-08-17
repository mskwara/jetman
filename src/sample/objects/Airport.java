package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Airport extends GameObject {
    private static final double MAX_GRAVITY_FACTOR = 0;

    private static final int AIRPORT_WIDTH = 150;
    private static final int AIRPORT_HEIGHT = 10;

    public Airport() {
        super(new Rectangle(AIRPORT_WIDTH, AIRPORT_HEIGHT, Color.GRAY));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
    }




    public static int getAirportWidth() {
        return AIRPORT_WIDTH;
    }

    public static int getAirportHeight() {
        return AIRPORT_HEIGHT;
    }
}
