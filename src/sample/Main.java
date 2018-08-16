package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.controller.GameController;
import sample.objects.Enemy;
import sample.objects.GameObject;

public class Main extends Application {
    //hej to kuba
    private Pane root;
    private GameController gameController;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(900, 900);

        gameController = new GameController();
        addGameObject(gameController.getPlayer(), 300, 300);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void addBullet(GameObject bullet, double x, double y) {
        gameController.addBullet(bullet);
        addGameObject(bullet, x, y);
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        gameController.addEnemy(enemy);
        addGameObject(enemy, x, y);
    }

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate() {
        gameController.updateGravity();
        root.getChildren().removeAll(gameController.gameObjectsToRemoveList());

        gameController.updateGameObjects();
        if (gameController.getPlayer().isShooting()) {
            shot();
        }

        if (Math.random() < 0.02) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    private void shot() {
        addBullet(gameController.fireBullet(), gameController.getPlayer().getView().getTranslateX(), gameController.getPlayer().getView().getTranslateY());
    }

    @Override
    public void start(Stage Stage) {
        Stage.setTitle("Hello World");
        Stage.setScene(new Scene(createContent()));
        Stage.getScene().setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    gameController.getPlayer().setTurningLeft(true);
                    break;
                case RIGHT:
                    gameController.getPlayer().setTurningRight(true);
                    break;
                case ENTER:
                    gameController.getPlayer().setShooting(true);
                    break;
                case UP:
                    gameController.getPlayer().setAccelerating(true);
                    break;
            }
        });

        Stage.getScene().setOnKeyReleased(e -> {

            switch (e.getCode()) {
                case LEFT:
                    gameController.getPlayer().setTurningLeft(false);
                    break;
                case RIGHT:
                    gameController.getPlayer().setTurningRight(false);
                    break;
                case ENTER:
                    gameController.getPlayer().setShooting(false);
                    break;
                case UP:
                    Point2D vector = new Point2D(gameController.getPlayer().getVelocity().getX(), gameController.getPlayer().getVelocity().getY());
                    gameController.getPlayer().getMultipleMotions().add(vector);
//                    for (int i = 0; i < gameController.getPlayer().getMultipleMotions().size(); i++) {
//                        System.out.println(gameController.getPlayer().getMultipleMotions().get(i));
//                    }
                    Point2D velo = new Point2D(0, 0);
                    gameController.getPlayer().setVelocity(velo);
                    gameController.getPlayer().setAccelerating(false);
                    break;
            }
        });
        Stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
