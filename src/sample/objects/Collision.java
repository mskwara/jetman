package sample.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Collision {
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
                }
            }
        }
        return list;
    }

    public static List<GameObject> getBulletsHitAirport(Player player, List<GameObject> airports) {
        List<GameObject> list = new ArrayList<>();
        for (GameObject bullet : player.getBullets()) {
            for (GameObject airport : airports) {
                if (bullet.isColliding(airport)) {
                    bullet.setAlive(false);
                    list.add(bullet);
                }
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
            for (GameObject player : players) {
                if (bullet.isColliding(player)) {
                    bullet.setAlive(false);
                    player.setAlive(false);
                    list.add(bullet);
                    list.add(player);
                }
            }
        }
        return list;

    }
}
