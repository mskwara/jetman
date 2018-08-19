package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.ParticleSystem.Emitter;
import sample.ParticleSystem.FireEmitter;
import sample.ParticleSystem.Particle;
import sample.controller.GameController;
import sample.objects.Enemy;
import sample.objects.GameObject;
import sample.objects.Gravity;
import sample.objects.Player;

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

        defaultGraphicContext();

        gameController = new GameController();
        addGameObject(gameController.getPlayer1(), 455, 320);
        addGameObject(gameController.getPlayer2(), 400, 320);

        gameController.getPlayer1().getView().setRotate(-90);
        gameController.getPlayer2().getView().setRotate(-90);

        addAirport(gameController.getAirport(), 400, 400);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void addBullet(Player player, GameObject bullet, double x, double y) {
        player.addBullet(bullet);
        addGameObject(bullet, x, y);
    }

    private void addBullets(Player player) {
        player.fire().forEach(bullet -> addBullet(player, bullet, player.getView().getTranslateX() + 50, player.getView().getTranslateY() + 50));
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        gameController.addEnemy(enemy);
        addGameObject(enemy, x, y);
    }
    private void addAirport(GameObject airport, double x, double y) {
        gameController.addAirport(airport);
        addGameObject(airport, x, y);
    }

    private void defaultGraphicContext() {
        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 900, 900);
    }

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate() {
        System.out.println("Bullets " + gameController.getPlayer1().getBullets().size());
        defaultGraphicContext();
        if (gameController.getPlayer1().getBullets().size() != 0) {
            for (GameObject bullet : gameController.getPlayer1().getBullets()) {
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
        double currentVelocityX = gameController.getPlayer1().getVelocity().getX();
        double currentVelocityY = gameController.getPlayer1().getVelocity().getY();
        for (int i = 0; i < gameController.getPlayer1().getMultipleMotions().size(); i++) {
            currentVelocityX += gameController.getPlayer1().getMultipleMotions().get(i).getX();
            currentVelocityY += gameController.getPlayer1().getMultipleMotions().get(i).getY();
        }
        currentVelocityY += gameController.getPlayer1().getGravityFactor() * Gravity.GRAVITY;
        gameController.getPlayer1().setCurrentVelocity(new Point2D(currentVelocityX, currentVelocityY));

        currentVelocityX = gameController.getPlayer2().getVelocity().getX();
        currentVelocityY = gameController.getPlayer2().getVelocity().getY();
        for (int i = 0; i < gameController.getPlayer2().getMultipleMotions().size(); i++) {
            currentVelocityX += gameController.getPlayer2().getMultipleMotions().get(i).getX();
            currentVelocityY += gameController.getPlayer2().getMultipleMotions().get(i).getY();
        }
        currentVelocityY += gameController.getPlayer2().getGravityFactor() * Gravity.GRAVITY;
        gameController.getPlayer2().setCurrentVelocity(new Point2D(currentVelocityX, currentVelocityY));


        gameController.updateGravity();
        root.getChildren().removeAll(gameController.gameObjectsToRemoveList());
        gameController.checkLanding();

        gameController.updateGameObjects();
        if (gameController.getPlayer1().isShooting()) {
            shot(gameController.getPlayer1());
            gameController.getPlayer1().setShooting(false);
        }
        if (gameController.getPlayer2().isShooting()) {
            shot(gameController.getPlayer2());
            gameController.getPlayer2().setShooting(false);
        }

        if (Math.random() < 0.02) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    private void shot(Player player) {
        addBullets(player);
    }

    @Override
    public void start(Stage Stage) {
        Stage.setTitle("Jetman");
        Stage.setScene(new Scene(createContent()));
        Stage.getScene().setOnKeyPressed(e -> onKeyPressed(e.getCode()));

        Stage.getScene().setOnKeyReleased(e -> onKeyReleased(e.getCode()));
        Stage.show();
    }

    private void onKeyPressed(KeyCode key) {
        switch (key) {
            case LEFT:
                gameController.getPlayer1().setTurningLeft(true);
                break;
            case RIGHT:
                gameController.getPlayer1().setTurningRight(true);
                break;
            case ENTER:
                gameController.getPlayer1().setShooting(true);
                break;
            case UP:
                gameController.getPlayer1().setAccelerating(true);
                break;
            case A:
                gameController.getPlayer2().setTurningLeft(true);
                break;
            case D:
                gameController.getPlayer2().setTurningRight(true);
                break;
            case TAB:
                gameController.getPlayer2().setShooting(true);
                break;
            case W:
                gameController.getPlayer2().setAccelerating(true);
                break;

        }
    }

    private void onKeyReleased(KeyCode key) {
        switch (key) {
            case LEFT:
                gameController.getPlayer1().setTurningLeft(false);
                break;
            case RIGHT:
                gameController.getPlayer1().setTurningRight(false);
                break;
            case ENTER:
                gameController.getPlayer1().setShooting(false);
                break;
            case UP:
                gameController.getPlayer1().getMultipleMotions().add(gameController.getPlayer1().getVelocity());
                gameController.getPlayer1().setVelocity(0, 0);
                gameController.getPlayer1().setAccelerating(false);
                break;
            case A:
                gameController.getPlayer2().setTurningLeft(false);
                break;
            case D:
                gameController.getPlayer2().setTurningRight(false);
                break;
            case TAB:
                gameController.getPlayer2().setShooting(false);
                break;
            case W:
                gameController.getPlayer2().getMultipleMotions().add(gameController.getPlayer2().getVelocity());
                gameController.getPlayer2().setVelocity(0, 0);
                gameController.getPlayer2().setAccelerating(false);
                break;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
