package sample;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.ParticleSystem.Emitter;
import sample.ParticleSystem.FireEmitter;
import sample.ParticleSystem.Particle;
import sample.controller.GameController;
import sample.objects.Bullet;
import sample.objects.Enemy;
import sample.objects.GameObject;
import sample.objects.Player;

import java.util.List;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    private Pane root;
    private GameController gameController;
    private Emitter emitter = new FireEmitter();
    private List<Particle> particles = new ArrayList<>();
    private GraphicsContext g;


    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(900, 900);

        Canvas canvas = new Canvas(900, 900);
        g = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 900, 900);

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
        gameController.getPlayer().addBullet(bullet);
        addGameObject(bullet, x, y);
    }

    private void addBullets(List<GameObject> bullets, double x, double y){
        bullets.forEach(bullet -> addBullet(bullet, x, y));
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

        if(gameController.getBullets().size() != 0) {
            for (GameObject bullet : gameController.getBullets()) {
                g.setGlobalAlpha(1.0);
                g.setGlobalBlendMode(BlendMode.SRC_OVER);
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, 900, 900);

                particles.addAll(emitter.emit(bullet.getView().getTranslateX(), bullet.getView().getTranslateY()));



                for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
                    Particle p = it.next();
                    p.update();

                    if (!p.isAlive()) {
                        it.remove();
                        continue;
                    }
                    p.render(g);


                }


            }
        }   else{
            g.setGlobalAlpha(1.0);
            g.setGlobalBlendMode(BlendMode.SRC_OVER);
            g.setFill(Color.BLACK);
            g.fillRect(0, 0, 900, 900);
                for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
                    Particle p = it.next();
                    p.update();

                    if (!p.isAlive()) {
                        it.remove();
                        continue;
                    }
                    p.render(g);


                }
        }

        gameController.updateGravity();
        root.getChildren().removeAll(gameController.gameObjectsToRemoveList());

        gameController.updateGameObjects();
        if (gameController.getPlayer().isShooting()) {
            shot();
            gameController.getPlayer().setShooting(false);
        }

        if (Math.random() < 0.02) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    private void shot() {
        addBullets((gameController.getPlayer()).fire(), gameController.getPlayer().getView().getTranslateX(), gameController.getPlayer().getView().getTranslateY());
    }

    @Override
    public void start(Stage Stage) {
        Stage.setTitle("Jetman");
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
