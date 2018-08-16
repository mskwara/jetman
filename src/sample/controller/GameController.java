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
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private GameObject player;


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
    }

    private void updatePlayer() {
        player.update();
        if (player.isAccelerating()) {
            if (player.getGravityFactor() > 1) {
                player.setGravityFactor(player.getGravityFactor() - 0.07);
            } else {
                player.setGravityFactor(1);
            }

            if ((player.getVelocity().getX() == 0 && player.getVelocity().getY() == 0)) { //statek nie porusza sie
                player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                player.setSpeed(1);
                System.out.println("1");
            } else { // statek porusza się
                if (player.getSpeed() <= 9 && player.getSpeed() >= 1) { //statek ma prędkość niemaksymalną
                    if (Helper.round(player.getVelocity().getX()) != Helper.round(Math.cos(Math.toRadians(player.getRotate())) * player.getSpeed())
                            || Helper.round(player.getVelocity().getY()) != Helper.round(Math.sin(Math.toRadians(player.getRotate())) * player.getSpeed())) {

                        player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                                Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(player.getSpeed())); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                        player.setSpeed(player.getSpeed());
                        System.out.println("2\n" + player.getVelocity().getX() + "\n" + Math.cos(Math.toRadians(player.getRotate())) * player.getSpeed());
                    } else {
                        player.setVelocity(new Point2D(player.getVelocity().getX() * 1.07, player.getVelocity().getY() * 1.07));
                        player.setSpeed(player.getSpeed() * 1.07);
                        System.out.println("3");
                    }
                } else if (player.getSpeed() < 1) {
                    player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                    player.setSpeed(1);
                    System.out.println("4");
                } else if (player.getSpeed() > 9) {  //gdy osiągnie max prędkość
                    player.setVelocity(new Point2D(player.getVelocity().getX(), player.getVelocity().getY()));
                    System.out.println("5");
                }
            }
        } else if (!player.isAccelerating()) {
            if (player.getGravityFactor() < 5) {
                player.setGravityFactor(player.getGravityFactor() + 0.07);
            } else {
                player.setGravityFactor(5);
            }

            /*if(player.getSpeed()>=0.04) {
                player.setVelocity(new Point2D(player.getVelocity().getX() / 1.07, player.getVelocity().getY() / 1.07));
                player.setSpeed(player.getSpeed() / 1.07);
            }
            else {
                player.setVelocity(new Point2D(0, 0));
                player.setSpeed(0);
            }*/
        }

        if (player.isTurningLeft()) {
            player.rotateLeft();
        } else if (player.isTurningRight()) {
            player.rotateRight();
        }
        playerInterialMovementService();
    }

    private void playerInterialMovementService() {
        //początek ruchu bezwładnościowego
        int i = 0;
        for (Point2D vector : player.getMultipleMotions()) {
            if (Math.abs(vector.getX()) >= 0.04 || Math.abs(vector.getY()) >= 0.04) {
                player.updatePosition(vector);
                Point2D newVector = new Point2D(vector.getX() * 0.98, vector.getY() * 0.98);
                player.getMultipleMotions().set(i, newVector);
//                System.out.println("nowy: " + player.getMultipleMotions().get(i));
            } else {
                Point2D newVector = new Point2D(0, 0);
                player.getMultipleMotions().set(i, newVector);
            }
            i++;
        }
//koniec bezwładności
    }

    public GameObject fireBullet() {
        Bullet bullet = new Bullet();
        if (player.getVelocity().getX() != 0 && player.getVelocity().getY() != 0) { //jeśli statek się porusza
            if (player.getVelocity().getX() != Math.cos(Math.toRadians(player.getRotate())) || player.getVelocity().getY() != Math.sin(Math.toRadians(player.getRotate()))) {
                bullet.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                        Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(10)); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
            } else {
                bullet.setVelocity(player.getVelocity().normalize().multiply(10));
            }
        } else {
            bullet.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(10));
        }
        return bullet;
    }

    public void updateGravity() {
        for(GameObject enemy : enemies) {
            if (enemy.getGravityFactor() < 5) {
                enemy.setGravityFactor(enemy.getGravityFactor() + 0.07);
            } else {
                enemy.setGravityFactor(5);
            }
        }
        for(GameObject bullet : bullets) {
            if (bullet.getGravityFactor() < 15) {
                bullet.setGravityFactor(bullet.getGravityFactor() + 0.07);
            } else {
                bullet.setGravityFactor(15);
            }
        }
        player.getView().setTranslateY(player.getView().getTranslateY() + GRAVITY * player.getGravityFactor());
        enemies.forEach(enemy -> enemy.getView().setTranslateY(enemy.getView().getTranslateY() + GRAVITY * enemy.getGravityFactor()));
        bullets.forEach(bullet -> bullet.getView().setTranslateY(bullet.getView().getTranslateY() + GRAVITY * bullet.getGravityFactor()));
    }
}
