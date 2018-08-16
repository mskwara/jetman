package sample.controller;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import sample.objects.Bullet;
import sample.objects.GameObject;
import sample.objects.Player;
import sample.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {

    private static final double GRAVITY = 1;
    private static final int BULLET_SPEED_FACTOR = 15;
    private static final double GAME_OBJECT_SPEED_FACTOR = 1.07;

    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private GameObject player;
    private double gravityFactor = 1;
    private double gravityFactorEnemy = 1;

    public GameController() {
        player = new Player();
    }

    public GameObject getPlayer() {
        return player;
    }

    public void addBullet(GameObject bullet) {
        bullets.add(bullet);
    }

    public void addEnemy(GameObject enemy) {
        enemies.add(enemy);
        System.out.println("Enemies: " + enemies.size());
    }

    public List<Node> gameObjectsToRemoveList() {
        List<Node> list = new ArrayList<>();
        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (enemy.getView().getTranslateY() > 900) {
                    enemy.setAlive(false);
                }
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    list.add(bullet.getView());
                    list.add(enemy.getView());
                }
            }
        }
        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        return list;
    }

    public void updateGameObjects() {
        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);
        updatePlayer();
        updateEnemies();
    }

    private void updatePlayer() {
        player.update();
        if (player.isAccelerating()) {
            if (gravityFactor > 1) {
                gravityFactor -= 0.07;
            } else {
                gravityFactor = 1;
            }

            if (!player.isMoving()) {
                player.setVelocity(createVector(player));
                player.setSpeed(1);
//                System.out.println("1");
            } else {
                if (hasNotMaxSpeed(player)) {
                    if (Helper.round(player.getVelocity().getX()) != Helper.round(cosRotation(player) * player.getSpeed())
                            || Helper.round(player.getVelocity().getY()) != Helper.round(sinRotation(player) * player.getSpeed())) {
                    //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                        player.setVelocity(createVector(player).normalize().multiply(player.getSpeed()));
//                        System.out.println("2\n" + player.getVelocity().getX() + "\n" + cosRotation(player) * player.getSpeed());
                    } else {
                        player.setVelocity(player.getVelocity().multiply(GAME_OBJECT_SPEED_FACTOR));
                        player.setSpeed(player.getSpeed() * GAME_OBJECT_SPEED_FACTOR);
//                        System.out.println("3");
                    }
                } else if (player.getSpeed() < 1) {
                    player.setVelocity(createVector(player));
                    player.setSpeed(1);
//                    System.out.println("4");
                }
            }
        } else if (!player.isAccelerating()) {
            if (gravityFactor < 5) {
                gravityFactor += 0.07;
            } else {
                gravityFactor = 5;
            }
        }

        if (player.isTurningLeft()) {
            player.rotateLeft();
        } else if (player.isTurningRight()) {
            player.rotateRight();
        }
        playerInterialMovementService();
    }
    private void updateEnemies(){
        if (gravityFactorEnemy < 5) {
            gravityFactorEnemy += 0.07;
        } else {
            gravityFactorEnemy = 5;
        }
    }

    private boolean hasNotMaxSpeed(GameObject object){
        return object.getSpeed() <= 9 && object.getSpeed() >= 1;
    }

    private double cosRotation(GameObject object){
        return Math.cos(Math.toRadians(object.getRotate()));
    }
    private double sinRotation(GameObject object){
        return Math.sin(Math.toRadians(object.getRotate()));
    }
    private Point2D createVector(GameObject object){
        return new Point2D(cosRotation(object),sinRotation(object));
    }

    private void playerInterialMovementService() {
        player.setMultipleMotions(player.getMultipleMotions().stream()
                .filter(vector -> Math.abs(vector.getX()) >= 0.04 || Math.abs(vector.getY()) >= 0.04)
                .map(vector -> vector.multiply(0.98))
                .collect(Collectors.toList()));
        player.getMultipleMotions().forEach(player::updatePosition);
    }

    public GameObject fireBullet() {
        Bullet bullet = new Bullet();
        if (player.isMoving()) { //jeśli statek się porusza
            if (player.getVelocity().getX() != cosRotation(player) || player.getVelocity().getY() != sinRotation(player)) {
                bullet.setVelocity(createVector(player).normalize().multiply(BULLET_SPEED_FACTOR)); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
            } else {
                bullet.setVelocity(player.getVelocity().normalize().multiply(BULLET_SPEED_FACTOR));
            }
        } else {
            bullet.setVelocity(createVector(player).normalize().multiply(BULLET_SPEED_FACTOR));
        }
        return bullet;
    }

    public void updateGravity() {
        player.getView().setTranslateY(player.getView().getTranslateY() + GRAVITY * gravityFactor);
        enemies.forEach(enemy -> enemy.getView().setTranslateY(enemy.getView().getTranslateY() + GRAVITY * gravityFactorEnemy));
    }
}
