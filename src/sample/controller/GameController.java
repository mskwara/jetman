package sample.controller;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import sample.objects.*;
import sample.utils.Helper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sample.objects.Player.PLAYER_SPEED_FACTOR;

public class GameController {
    private List<GameObject> enemies = new ArrayList<>();
    private List<GameObject> airports = new ArrayList<>();
    private Player player1;
    private Player player2;
    private Airport airport;

    public GameController() {
        player1 = new Player(Color.BLUE);
        player2 = new Player(Color.YELLOW);
        airport = new Airport();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Airport getAirport() {
        return airport;
    }

    public void addAirport(GameObject airport) {
        airports.add(airport);
        //System.out.println("Airports: " + airports.size());
    }

    public void addEnemy(GameObject enemy) {
        enemies.add(enemy);
        //System.out.println("Enemies: " + enemies.size());
    }

    public void checkLanding() {
        checkLandingPlayerOnAirport(player1, airport);
        checkLandingPlayerOnAirport(player2, airport);
    }

    private void checkLandingPlayerOnAirport(Player player, Airport airport) {
        //System.out.println("currentVel: " + player.getCurrentVelocity());
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

        //bullet uderza w player
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitPlayersCollisions(Arrays.asList(player1, player2))));

        //bullet uderza w enemy
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsEnemiesCollisions(player1, enemies)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsEnemiesCollisions(player2, enemies)));
        //bullet wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(player1.getBullets())));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(player2.getBullets())));

        //enemy wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(enemies)));

        //bullet uderza w lotnisko
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitAirport(player1, airports)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitAirport(player2, airports)));

        player1.getBullets().removeIf(GameObject::isDead);
        player2.getBullets().removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);


        return list;
    }

    public void updateGameObjects() {
        player1.getBullets().forEach(GameObject::update);
        player2.getBullets().forEach(GameObject::update);
        enemies.forEach(GameObject::update);
        updatePlayer(player1);
        updatePlayer(player2);
        //updateEnemies();
    }

    private void updatePlayer(Player player) {
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
        Gravity.updateGameObjectsGravity(player1.getBullets());
        Gravity.updateGameObjectsGravity(player2.getBullets());
        Gravity.updateGameObjectsGravity(enemies);
        Gravity.updatePlayersGravity(Collections.singletonList(player1));
        Gravity.updatePlayersGravity(Collections.singletonList(player2));
    }
}
