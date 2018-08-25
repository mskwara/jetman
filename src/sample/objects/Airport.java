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

    public boolean canPlayerLanding(Player player) {
        return !player.isAccelerating() && player.isColliding(this) && hasVerticalPosition(player)
                && hasSlowSpeed(player) && isAboveAirport(player);
    }

    public boolean isAboveAirport(Player player) {
        return player.getView().getTranslateY() + Player.PLAYER_WIDTH / 2 + Airport.AIRPORT_HEIGHT / 2 <= getView().getTranslateY();
    }

    public boolean hasVerticalPosition(Player player) {
        return (player.getRotate() >= -99 && player.getRotate() <= -81) || (player.getRotate() >= 261 && player.getRotate() <= 279);
    }

    public boolean hasSlowSpeed(Player player) {
        return Math.abs(player.getCurrentVelocity().getX()) <= 6 && player.getCurrentVelocity().getY() <= 2.2;
    }
}
