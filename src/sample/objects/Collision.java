package sample.objects;

import javafx.geometry.Point2D;
import sample.Main;
import sample.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sample.objects.Player.PLAYER_HEIGHT;
import static sample.objects.Player.PLAYER_WIDTH;

public class Collision {

    private static List<GameObject> pixelsToRemove = new ArrayList<>();

    private static void destroyPlayerByBullet(Player player, GameObject bullet){
        double centerPosX = player.getView().getTranslateX()+PLAYER_WIDTH/2;
        double centerPosY = player.getView().getTranslateY()+PLAYER_HEIGHT/2;
        Double startPosX = centerPosX - 20;
        Double startPosY = centerPosY - 20;
        pixelsToRemove = new ArrayList<>();

        int pixelCount = 0;
        for(int i = startPosX.intValue() ; i < startPosX.intValue()+40 ; i=i+4){
            for(int j = startPosY.intValue() ; j < startPosY.intValue()+40 ; j=j+4){
                GameObject tempObj = new GameObject();
                Main.addGameObject(tempObj, i, j);


                if(tempObj.isColliding(player) && pixelCount < 80){
                    Pixel pixel = new Pixel(player.getColor());
                    pixel.setVelocity(new Point2D(bullet.getVelocity().getX()/2 + Math.random()*3, bullet.getVelocity().getY()/2 + Math.random()*3).normalize().multiply(5));
                    GameController.getPixels().add(pixel);
                    Main.addGameObject(pixel, i, j);
                    pixelCount++;
                }
                pixelsToRemove.add(tempObj);
            }
        }
        player.setCurrentVelocity(new Point2D(0,0));
        player.setVelocity(0,0);
        player.setGravityFactor(1);
        player.getView().setTranslateX(-50);
        player.getView().setTranslateY(-50);
        player.setShooting(false);

    }
    public static void destroyPlayerByCrash(Player player){
        double centerPosX = player.getView().getTranslateX()+PLAYER_WIDTH/2;
        double centerPosY = player.getView().getTranslateY()+PLAYER_HEIGHT/2;
        Double startPosX = centerPosX - 20;
        Double startPosY = centerPosY - 20;
        pixelsToRemove = new ArrayList<>();

        int pixelCount = 0;
        for(int i = startPosX.intValue() ; i < startPosX.intValue()+40 ; i=i+4){
            for(int j = startPosY.intValue() ; j < startPosY.intValue()+40 ; j=j+4){
                GameObject tempObj = new GameObject();
                Main.addGameObject(tempObj, i, j);


                if(tempObj.isColliding(player) && pixelCount < 80){
                    Pixel pixel = new Pixel(player.getColor());
                    pixel.setVelocity(new Point2D(player.getCurrentVelocity().getX() + Math.random()*3, player.getCurrentVelocity().getY() + Math.random()*3).multiply(0.8));
                    GameController.getPixels().add(pixel);
                    Main.addGameObject(pixel, i, j);
                    pixelCount++;
                }
                pixelsToRemove.add(tempObj);
            }
        }

        player.setCurrentVelocity(new Point2D(0,0));
        player.setVelocity(0,0);
        player.setGravityFactor(1);
        player.getView().setTranslateX(-50);
        player.getView().setTranslateY(-50);
        player.setShooting(false);
    }

    public static List<GameObject> getObjectsOutOfMap(List<GameObject> objects) {
        return objects.stream()
                .filter(Collision::isObjectOutOfMap)
                .peek(object -> {
                    object.setAlive(false);
                })
                .collect(Collectors.toList());
    }

    private static boolean isObjectOutOfMap(GameObject object) {
        return object.getView().getTranslateY() > 900 || object.getView().getTranslateY() < 0
                || object.getView().getTranslateX() > 900 || object.getView().getTranslateX() < 0;
    }

    public static List<GameObject> getBulletsEnemiesCollisions(Player player, List<GameObject> enemies) {
        List<GameObject> list = new ArrayList<>();
        for (GameObject bullet : player.getBullets()) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    list.add(bullet);
                    list.add(enemy);
                    player.getDiedBullets().add(bullet);
                }
            }
        }
        return list;
    }

    public static List<GameObject> getBulletsHitAirport(Player player, List<Airport> airports) {
        List<GameObject> list = new ArrayList<>();
        for (GameObject bullet : player.getBullets()) {
            for (GameObject airport : airports) {
                if (bullet.isColliding(airport)) {
                    bullet.setAlive(false);
                    list.add(bullet);
                    player.getDiedBullets().add(bullet);
                }
            }
        }
        return list;
    }

    public static List<GameObject> getPlayerHitAirport(Player player, List<Airport> airports) {
        List<GameObject> list = new ArrayList<>();
            for (Airport airport : airports) {
                if (player.isColliding(airport) && (!airport.hasSlowSpeed(player) || !airport.hasVerticalPosition(player) || !airport.isAboveAirport(player))) {
                    System.out.println("tutaj: "+player.getCurrentVelocity());
                    player.setAlive(false);
                    list.add(player);
                    destroyPlayerByCrash(player);

                }
            }

        return list;
    }

    public static List<GameObject> getBulletsHitPlayersCollisions(List<Player> players) {
        List<GameObject> allBullets = players.stream()
                .flatMap(player -> player.getBullets().stream())
                .collect(Collectors.toList());
        List<GameObject> list = new ArrayList<>();
        for (GameObject bullet : allBullets) {
            for (Player player : players) {
                if (bullet.isColliding(player)) {
                    bullet.setAlive(false);
                    player.setAlive(false);
                    list.add(bullet);
                    list.add(player);
                    destroyPlayerByBullet(player, bullet);
                    player.getDiedBullets().add(bullet);
                }
            }
        }
        return list;

    }
    public static List<GameObject> removePixels() {
        List<GameObject> list = new ArrayList<>();
            for(GameObject obj : pixelsToRemove){
                list.add(obj);
            }

        return list;

    }


}
