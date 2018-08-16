package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static sample.objects.Gun.BULLET_SPEED_FACTOR;

public class Player extends GameObject {
    public static final double PLAYER_SPEED_FACTOR = 1.07;
    private Weapon weapon;
    private List<GameObject> bullets = new ArrayList<>();

    public Player() {
        super(new Rectangle(40, 20, Color.BLUE));
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
}