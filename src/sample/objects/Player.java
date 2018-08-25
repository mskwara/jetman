package sample.objects;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sample.objects.Gun.BULLET_SPEED_FACTOR;

public class Player extends GameObject {
    public static final double PLAYER_SPEED_FACTOR = 1.1;
    private static final double MAX_GRAVITY_FACTOR = 5;
    public static final double MAX_HEALTH = 100;
    private Weapon weapon;

    public static final int PLAYER_WIDTH = 40;
    public static final int PLAYER_HEIGHT = 20;
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> diedBullets = new ArrayList<>();
    private Color color;
    private int score = 0;
    private double health = MAX_HEALTH;
    private String name = "Player";
    private double defaultXPosition;
    private double defaultYPosition;


    public Player(String name, Color color, double x, double y) {
        super(new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, color));
        setMaxGravityFactor(MAX_GRAVITY_FACTOR);
        this.color = color;
        this.name = name;
        this.defaultXPosition = x;
        this.defaultYPosition = y;
        weapon = new Gun();
    }

    public double getHealth() {
        return health;
    }

    public void changeHealth(double amount) {
        health = health <= 100 ? health + amount : MAX_HEALTH;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public double getDefaultXPosition() {
        return defaultXPosition;
    }

    public double getDefaultYPosition() {
        return defaultYPosition;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScroe(int amount) {
        this.score += amount;
    }

    public String getScoreLabel() {
        return String.format("%s: %d\nHealth: %d%%\nShots: %d", name, score, (int) health, ((Gun) weapon).getBulletsNumber());
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

    public List<GameObject> fire() {
        List<GameObject> firedBullets = weapon.fire();
        for (GameObject bullet : firedBullets) {
            if (hasSpeed()) { //jeśli statek się porusza
                //if (getVelocity().getX() != cosRotation() || getVelocity().getY() != sinRotation()) {
                if (Helper.round(getVelocity().getX()) != Helper.round(cosRotation() * getSpeed())
                        || Helper.round(getVelocity().getY()) != Helper.round(sinRotation() * getSpeed())) {
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

    public void moveInterial() {
        setMultipleMotions(getMultipleMotions().stream()
                .filter(vector -> Math.abs(vector.getX()) >= 0.04 || Math.abs(vector.getY()) >= 0.04)
                .map(vector -> vector.multiply(0.98))
                .collect(Collectors.toList()));
        getMultipleMotions().forEach(this::updatePosition);
    }

    public void changeCurrentVelocity() {
        if (!isOnGround()) {
            Point2D currentVelocity = getVelocity().add(getMultipleMotions().stream().reduce(Point2D::add).orElse(new Point2D(0, 0)));
            currentVelocity = currentVelocity.add(0, getGravityFactor() * 0.7 * Gravity.GRAVITY);
            if (getView().getRotate() == -90 || getView().getRotate() == -270
                    || getView().getRotate() == 90 || getView().getRotate() == 270) {
                currentVelocity = new Point2D(0, currentVelocity.getY());
            }
            setCurrentVelocity(currentVelocity);
        }
    }

    public void respawn() {
        getView().setTranslateX(getDefaultXPosition());
        getView().setTranslateY(getDefaultYPosition());
        getView().setRotate(-90);
        setVelocity(0, 0);
        setMultipleMotions(Collections.emptyList());
        setGravityFactor(1);
        setHealth(MAX_HEALTH);
    }

    public Color getColor() {
        return color;
    }

    public void reloadWeapon() {
        ((Gun) weapon).changeBulletsNumber(0.08);
    }
}