package sample.objects;

import java.util.List;

public class Gravity {
    private static final double GRAVITY = 1;

    public static void updateGameObjectsGravity(List<GameObject> objects) {
        objects.forEach(Gravity::updateGameObjectGravity);
    }

    private static void updateGameObjectGravity(GameObject object){
        if (object.getGravityFactor() < object.getMaxGravityFactor()) {
            object.changeGravityFactor(0.07);
        } else {
            object.setGravityFactor(object.getMaxGravityFactor());
        }
        object.getView().setTranslateY(object.getView().getTranslateY() + GRAVITY * object.getGravityFactor());
    }

    public static void updatePlayersGravity(List<Player> players) {
        players.forEach(Gravity::updatePlayerGravity);
    }

    private static void updatePlayerGravity(Player player){
        if(!player.isOnGround()) {
            if (player.isAccelerating()) {
                if (player.getGravityFactor() > 1) {
                    player.changeGravityFactor(-0.07);
                } else {
                    player.setGravityFactor(1);
                }
            } else {
                if (player.getGravityFactor() < player.getMaxGravityFactor()) {
                    player.changeGravityFactor(0.07);

                } else {
                    player.setGravityFactor(player.getMaxGravityFactor());
                }
            }
            player.getView().setTranslateY(player.getView().getTranslateY() + GRAVITY * player.getGravityFactor());
        }
        else {
            player.setGravityFactor(0);
        }
    }
}
