package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends GameObject {
    public Player() {
        super(new Rectangle(40, 20, Color.BLUE));
    }
}