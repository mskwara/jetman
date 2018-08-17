package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sample.objects.Gun.BULLET_SPEED_FACTOR;

public class Player extends GameObject {
    public static final double PLAYER_SPEED_FACTOR = 1.07;
    private static final double MAX_GRAVITY_FACTOR = 5;
    private Weapon weapon;

    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 20;
    private List<GameObject> bullets = new ArrayList<>();

    public Player() {
        super(new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, Color.BLUE));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
        weapon = new Gun();
    }

    public List<GameObject> getBullets() {
        return bullets;
    }

    public void addBullet(GameObject bullet) {
        bullets.add(bullet);
    }

    public List<GameObject> fire(){
        List<GameObject> firedBullets = weapon.fire();
        for(GameObject bullet : firedBullets){
            if (hasSpeed()) { //jeśli statek się porusza
                if (getVelocity().getX() != cosRotation() || getVelocity().getY() != sinRotation()) {
                    bullet.setVelocity(createVector().normalize().multiply(BULLET_SPEED_FACTOR)); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                } else {
                    bullet.setVelocity(getVelocity().normalize().multiply(BULLET_SPEED_FACTOR));
                }
            } else {
                bullet.setVelocity(createVector().normalize().multiply(BULLET_SPEED_FACTOR));
            }
        }
        return firedBullets;
    }

    public void moveInterial(){
        setMultipleMotions(getMultipleMotions().stream()
                .filter(vector -> Math.abs(vector.getX()) >= 0.04 || Math.abs(vector.getY()) >= 0.04)
                .map(vector -> vector.multiply(0.98))
                .collect(Collectors.toList()));
        getMultipleMotions().forEach(this::updatePosition);
    }

    public static int getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    public static int getPlayerHeight() {
        return PLAYER_HEIGHT;
    }
}