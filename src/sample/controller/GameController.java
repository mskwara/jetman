package sample.controller;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sample.Main;
import sample.objects.*;
import sample.utils.Helper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sample.objects.Player.PLAYER_SPEED_FACTOR;

public class GameController {
    private List<GameObject> enemies = new ArrayList<>();
    private List<Airport> airports = new ArrayList<>();
    private static List<GameObject> pixels = new ArrayList<>();
    private List<Pixel> mapPixelList = new ArrayList<>();

    private Player player1;
    private Player player2;
    private Airport airport;
    private Text player1Label;
    private Text player2Label;
    private Text endGameLabel;

    public void createMapPixels(String src){
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(new File(src));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < mapImage.getWidth() ; i=i+3){
            for(int j = 0; j < mapImage.getHeight() ; j=j+3){
                int color = mapImage.getRGB(i, j);
                int red   = (color >> 16) & 0xFF;
                int green = (color >> 8)  & 0xFF;
                int blue  = (color >> 0)  & 0xFF;


                if(red == 0 && green == 255 && blue == 0){
                    Pixel pixel = new Pixel(Color.GREEN, 3);
                    //System.out.println(red+" "+green+" "+blue);
                    pixel.setGravityWorking(false);
                    pixel.setVelocity(0,0);
                    mapPixelList.add(pixel);
                    Main.addGameObject(pixel, i, j);
                }
            }
        }

    }


    public GameController() {
        player1 = new Player("Michal", Color.BLUE, 455, 320);
        player2 = new Player("Kuba", Color.YELLOW, 400, 320);
        airport = new Airport();
        player1Label = new Text();
        player2Label = new Text();
        endGameLabel = new Text();
        setLabel(player1Label, 20, 500, 50, Color.WHITE, true);
        setLabel(player2Label, 20, 50, 50, Color.WHITE, true);
        setLabel(endGameLabel, 50, 320, 300, Color.RED, false);
    }

    private void setLabel(Text label, int size, double x, double y, Color color, boolean visible) {
        label.setFont(Font.font("Verdana", size));
        label.setX(x);
        label.setY(y);
        label.setFill(color);
        label.setText("");
        label.setVisible(visible);
    }

    public Text getEndGameLabel() {
        return endGameLabel;
    }

    public Text getPlayer1Label() {
        return player1Label;
    }

    public Text getPlayer2Label() {
        return player2Label;
    }

    public static List<GameObject> getPixels() {
        return pixels;
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

    public void addAirport(Airport airport) {
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
            player.setCurrentVelocity(new Point2D(0, 0));
            player.setMultipleMotions(Collections.emptyList());
            player.getView().setRotate(-90);
            player.setOnGround(true);
        } else {
            player.setOnGround(false);
            //player.setAlive(false);
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
        player1.getDiedBullets().addAll(Collision.getObjectsOutOfMap(player1.getBullets()));
        player2.getDiedBullets().addAll(Collision.getObjectsOutOfMap(player2.getBullets()));

        Collision.getPlayersOutOfMap(Arrays.asList(player1, player2));

        //enemy wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(enemies)));

        //pixel wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getObjectsOutOfMap(pixels)));

        //pixel mapowy wypada za mapę
        list.addAll(Helper.gameObjectList2NodeList(Collision.getPixelsOutOfMap(mapPixelList)));

        //bullet uderza w lotnisko
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitAirport(player1, airports)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsHitAirport(player2, airports)));

        //player uderza w lotnisko
        list.addAll(Helper.gameObjectList2NodeList(Collision.getPlayerHitAirport(player1, player2, airports)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getPlayerHitAirport(player2, player1, airports)));

        list.addAll(Helper.gameObjectList2NodeList(Collision.getPlayerHitWall(player1, player2, mapPixelList)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getPlayerHitWall(player2, player1, mapPixelList)));

        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsMapCollisions(player1, mapPixelList)));
        list.addAll(Helper.gameObjectList2NodeList(Collision.getBulletsMapCollisions(player2, mapPixelList)));


        //usuwa niepotrzebne pixele
        list.addAll(Helper.gameObjectList2NodeList(Collision.removePixels()));

        player1.getBullets().removeIf(GameObject::isDead);
        player2.getBullets().removeIf(GameObject::isDead);

        enemies.removeIf(GameObject::isDead);
        pixels.removeIf(GameObject::isDead);
        mapPixelList.removeIf(GameObject::isDead);

        return list;
    }

    public void updateGameObjects() {

        player1.getBullets().forEach(GameObject::update);
        player2.getBullets().forEach(GameObject::update);
        enemies.forEach(GameObject::update);
        pixels.forEach(GameObject::update);
        for(Pixel p : mapPixelList){
            if(p.getVelocity().getX() != 0){
                p.update();
            }
        }
        updatePlayer(player1);
        updatePlayer(player2);
        updateLabels();
        //updateEnemies();
    }

    public boolean isEndGame() {
        return playerWins(player1) || playerWins(player2);
    }

    private boolean playerWins(Player player) {
        if (player.getScore() > Score.WIN_SCORE) {
            endGameLabel.setText(player.getName() + " wins!");
            endGameLabel.setVisible(true);
            return true;
        }
        return false;
    }

    private void updateLabels() {
        player1Label.setText(player1.getScoreLabel());
        player2Label.setText(player2.getScoreLabel());

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

        if (player.isOnGround()) {
            player.reloadWeapon();
            player.changeHealth(0.2);
        }
    }

    private boolean hasNotMaxSpeed(GameObject object) {
        return object.getSpeed() <= 9 && object.getSpeed() >= 1;
    }

    public void updateGravity() {
        Gravity.updateGameObjectsGravity(player1.getBullets());
        Gravity.updateGameObjectsGravity(player2.getBullets());
        Gravity.updateGameObjectsGravity(enemies);
        Gravity.updateGameObjectsGravity(pixels);
        int i = 0;
        for(Pixel pixel : mapPixelList) {
            if(pixel.isGravityWorking()) {
                Gravity.updateGameObjectsGravity(Collections.singletonList(mapPixelList.get(i)));
            }
            i++;
        }
        Gravity.updatePlayersGravity(Collections.singletonList(player1));
        Gravity.updatePlayersGravity(Collections.singletonList(player2));
    }

    public void changePlayersCurrentVelocity() {
        player1.changeCurrentVelocity();
        player2.changeCurrentVelocity();
    }

}
