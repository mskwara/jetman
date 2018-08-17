package sample.controller;

import javafx.scene.Node;
import sample.objects.*;
import sample.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sample.objects.Player.PLAYER_SPEED_FACTOR;

public class GameController {
    private List<GameObject> enemies = new ArrayList<>();
    private List<GameObject> airports = new ArrayList<>();
    private Player player;
    private Airport airport;

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

    public void checkLanding() {
        System.out.println("currentVel: " + player.getCurrentVelocity());
        if (airport.canPlayerLanding(player)) {
            player.setVelocity(0, 0);
            player.setMultipleMotions(Collections.emptyList());
            player.getView().setRotate(-90);
            player.setOnGround(true);
        } else {
            player.setOnGround(false);
        }
    }

    public List<Node> gameObjectsToRemoveList() {
        List<Node> list = new ArrayList<>();

        //bullet uderza w enemy
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsEnemiesCollisions(player, enemies)));
        //bullet wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(player.getBullets())));

        //enemy wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(enemies)));

        //bullet uderza w lotnisko
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitAirport(player, airports)));

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

    private boolean hasNotMaxSpeed(GameObject object) {
        return object.getSpeed() <= 9 && object.getSpeed() >= 1;
    }

    public void updateGravity() {
        Gravity.updateGameObjectsGravity(player.getBullets());
        Gravity.updateGameObjectsGravity(enemies);
        Gravity.updatePlayersGravity(Collections.singletonList(player));
    }
}
