package sample.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sample.objects.Gun.BULLET_SPEED_FACTOR;

public class Player extends GameObject {
    public static final double PLAYER_SPEED_FACTOR = 1.07;
    private static final double MAX_GRAVITY_FACTOR = 5;
    private Weapon weapon;

    public static final int PLAYER_WIDTH = 40;
    public static final int PLAYER_HEIGHT = 20;
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> diedBullets = new ArrayList<>();

    public Player(Color color) {
        super(new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, color));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
        weapon = new Gun();
    }

    public List<GameObject> getBullets() {
        return bullets;
    }


    public List<GameObject> getDiedBullets() {
        return diedBullets;
    }


    public void addBullet(GameObject bullet) {
        bullets.add(bullet);
    }

    public List<GameObject> fire(){
        List<GameObject> firedBullets = weapon.fire();
        for(GameObject bullet : firedBullets){
            if (hasSpeed()) { //jeśli statek się porusza
                //if (getVelocity().getX() != cosRotation() || getVelocity().getY() != sinRotation()) {
                    if(Helper.round(getVelocity().getX()) != Helper.round(cosRotation() * getSpeed())
                            || Helper.round(getVelocity().getY()) != Helper.round(sinRotation() * getSpeed())){
                    bullet.setVelocity(createVector().normalize().multiply(BULLET_SPEED_FACTOR)); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                    //System.out.println("1");
                } else {
                    bullet.setVelocity(getVelocity().normalize().multiply(BULLET_SPEED_FACTOR));
                    //System.out.println("2");
                }
            } else {
                bullet.setVelocity(createVector().normalize().multiply(BULLET_SPEED_FACTOR));
                //System.out.println("3");
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
}