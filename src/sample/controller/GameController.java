package sample.controller;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import sample.objects.Airport;
import sample.objects.GameObject;
import sample.objects.Gravity;
import sample.objects.Player;
import sample.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sample.objects.Player.PLAYER_SPEED_FACTOR;
public class GameController {
    public static double getGRAVITY() {
        return GRAVITY;
    }

    private static final double GRAVITY = 1;
    private static final int BULLET_SPEED_FACTOR = 10;
    private static final double GAME_OBJECT_SPEED_FACTOR = 1.07;
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private List<GameObject> airports = new ArrayList<>();
    private Player player;
    private Airport airport;

    public List<GameObject> getBullets() {
        return bullets;
    }





    public GameController() {
        player = new Player();
        airport = new Airport();
    }

    public Player getPlayer() {
        return player;
    }
    public Airport getAirport() {
        return airport;
    }
    public void addAirport(GameObject airport) {
        airports.add(airport);
        System.out.println("Airports: " + airports.size());
    }

    public void addEnemy(GameObject enemy) {
        enemies.add(enemy);
        System.out.println("Enemies: " + enemies.size());
    }
    public void checkLanding(){
        System.out.println("currentVel: " + player.getCurrentVelocity());
        if(!player.isAccelerating() && player.isColliding(airport) && (player.getRotate()>=-96 && player.getRotate()<=-84)
                && player.getCurrentVelocity().getX()<=3  && player.getCurrentVelocity().getY()<=3 && player.getCurrentVelocity().getY()>=0
                && player.getView().getTranslateY()+player.getPlayerWidth()/2+airport.getAirportHeight()/2 <= airport.getView().getTranslateY()){
            player.setVelocity(new Point2D(0,0));
            List<Point2D> emptyList = new ArrayList<>();
            player.setMultipleMotions(emptyList);
            player.getView().setRotate(-90);
            player.setOnGround(true);
        } else{
            player.setOnGround(false);
        }
    }

    public List<Node> gameObjectsToRemoveList() {
        List<Node> list = new ArrayList<>();
        //bullet uderza w enemy oraz bullet wypada za mapę
        for (GameObject bullet : player.getBullets()) {
            if (bullet.getView().getTranslateY() > 900 || bullet.getView().getTranslateY() < 0
                    || bullet.getView().getTranslateX() > 900 || bullet.getView().getTranslateX() < 0) {
                bullet.setAlive(false);
                list.add(bullet.getView());
            }
                for (GameObject enemy : enemies) {
                    if (bullet.isColliding(enemy)) {
                        bullet.setAlive(false);
                        enemy.setAlive(false);
                        list.add(bullet.getView());
                        list.add(enemy.getView());
                    }
                }
        }
        //enemy wypada za mapę
        for (GameObject enemy : enemies) {
            if (enemy.getView().getTranslateY() > 900 || enemy.getView().getTranslateY() < 0
                    || enemy.getView().getTranslateX() > 900 || enemy.getView().getTranslateX() < 0) {
                enemy.setAlive(false);
                list.add(enemy.getView());
            }
        }

//bullet uderza w lotnisko
        for (GameObject bullet : player.getBullets()) {
            for (GameObject airport : airports) {
                if (bullet.isColliding(airport)) {
                    bullet.setAlive(false);
                    list.add(bullet.getView());
                }
            }
        }

        player.getBullets().removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        return list;
    }

    public void updateGameObjects() {
        player.getBullets().forEach(GameObject::update);
        enemies.forEach(GameObject::update);
        updatePlayer();
        //updateEnemies();
    }

    private void updatePlayer() {
        player.update();
        if (player.isAccelerating()) {
            if (!player.hasVelocity()) {
                player.setVelocity(player.createVector());
                player.setSpeed(1);
//                System.out.println("1");
            } else {
                if (hasNotMaxSpeed(player)) {
                    if (Helper.round(player.getVelocity().getX()) != Helper.round(player.cosRotation() * player.getSpeed())
                            || Helper.round(player.getVelocity().getY()) != Helper.round(player.sinRotation() * player.getSpeed())) {
                    //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                        player.setVelocity(player.createVector().normalize().multiply(player.getSpeed()));
//                        System.out.println("2\n" + player.getVelocity().getX() + "\n" + cosRotation(player) * player.getSpeed());
                    } else {
                        player.setVelocity(player.getVelocity().multiply(PLAYER_SPEED_FACTOR));
                        player.setSpeed(player.getSpeed() * PLAYER_SPEED_FACTOR);
//                        System.out.println("3");
                    }
                } else if (player.getSpeed() < 1) {
                    player.setVelocity(player.createVector());
                    player.setSpeed(1);
//                    System.out.println("4");
                }
            }
        }

        if (player.isTurningLeft()) {
            player.rotateLeft();
        } else if (player.isTurningRight()) {
            player.rotateRight();
        }
        player.moveInterial();
    }

    private boolean hasNotMaxSpeed(GameObject object){
        return object.getSpeed() <= 9 && object.getSpeed() >= 1;
    }

    public void updateGravity() {
        Gravity.updateGameObjectsGravity(player.getBullets());
        Gravity.updateGameObjectsGravity(enemies);
        Gravity.updatePlayersGravity(Collections.singletonList(player));
    }
}
