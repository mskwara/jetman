package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
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
import sample.objects.Airport;
import sample.objects.Enemy;
import sample.objects.GameObject;
import sample.objects.Player;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static Pane root;
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

        gameController.createMapPixels("map.bmp");
        addGameObject(gameController.getPlayer1(), 455, 320);
        addGameObject(gameController.getPlayer2(), 400, 320);
        root.getChildren().add(gameController.getPlayer1Label());
        root.getChildren().add(gameController.getPlayer2Label());
        root.getChildren().add(gameController.getEndGameLabel());

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
        double alfa = Math.toRadians(player.getRotate());
        double x = (Player.PLAYER_WIDTH / 2 + 20) * Math.cos(alfa);   //rysunek poglądowy w paincie
        double y = (Player.PLAYER_WIDTH / 2 + 20) * Math.sin(alfa);
        player.fire().forEach(bullet -> addBullet(player, bullet, player.getView().getTranslateX() + (Player.PLAYER_WIDTH / 2 - 5) + x, player.getView().getTranslateY() + (Player.PLAYER_HEIGHT / 2) + y));
        //powyższa linijka to pozycja statku + jego połowa wymiatu, żeby mieć punkt środka pojazdu + odpowiedni x i y aby pocisk strzelał z przodu a nie ze środka
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        gameController.addEnemy(enemy);
        addGameObject(enemy, x, y);
    }

    private void addAirport(Airport airport, double x, double y) {
        gameController.addAirport(airport);
        addGameObject(airport, x, y);
    }


    private void defaultGraphicContext() {
        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 900, 900);
    }

    public static void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate() {
        //System.out.println(gameController.getPlayer1().getCurrentVelocity()+" gravityF: "+gameController.getPlayer1().getGravityFactor());
        //System.out.println(gameController.getPlayer1().getBullets().size());
        //System.out.println(gameController.getPlayer1().getView().getTranslateX()+" "+gameController.getPlayer1().getView().getTranslateY());
        System.out.println("Rotate " + gameController.getPlayer1().getRotate());
        defaultGraphicContext();
        if (gameController.getPlayer1().getBullets().size() != 0 || gameController.getPlayer2().getBullets().size() != 0) {
            for (GameObject bullet : gameController.getPlayer1().getBullets()) {
                drawParticles(bullet.getView().getTranslateX(), bullet.getView().getTranslateY(), bullet);
            }
            for (GameObject bullet : gameController.getPlayer2().getBullets()) {
                drawParticles(bullet.getView().getTranslateX(), bullet.getView().getTranslateY(), bullet);
            }
        }
        for (GameObject diedBullet : gameController.getPlayer1().getDiedBullets()) {
            drawDiedParticles(diedBullet);
        }
        for (GameObject diedBullet : gameController.getPlayer2().getDiedBullets()) {
            drawDiedParticles(diedBullet);
        }

        gameController.changePlayersCurrentVelocity();
        //
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
        gameController.isEndGame();


    }

    private void shot(Player player) {
        addBullets(player);
    }

    private void drawParticles(double x, double y, GameObject bullet) {
        particles.addAll(emitter.emit(x, y, bullet));

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            if (p.getEmitter() == bullet) {
                p.update();

                if (!p.isAlive()) {
                    particles.remove(i);
                    continue;
                }
                p.render(g);
            }
        }

    }

    private void drawDiedParticles(GameObject diedBullet) {

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            if (p.getEmitter() == diedBullet) {
                p.update();

                if (!p.isAlive()) {
                    particles.remove(i);
                    continue;
                }
                p.render(g);
            }
        }
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
                if (gameController.getPlayer1().isAlive()) {
                    gameController.getPlayer1().setTurningLeft(true);
                }
                break;
            case RIGHT:
                if (gameController.getPlayer1().isAlive()) {
                    gameController.getPlayer1().setTurningRight(true);
                }
                break;
            case ENTER:
                if (gameController.getPlayer1().isAlive()) {
                    gameController.getPlayer1().setShooting(true);
                }
                break;
            case UP:
                if (gameController.getPlayer1().isAlive()) {
                    gameController.getPlayer1().setAccelerating(true);
                }
                break;
            case A:
                if (gameController.getPlayer2().isAlive()) {
                    gameController.getPlayer2().setTurningLeft(true);
                }
                break;
            case D:
                if (gameController.getPlayer2().isAlive()) {
                    gameController.getPlayer2().setTurningRight(true);
                }
                break;
            case TAB:
                if (gameController.getPlayer2().isAlive()) {
                    gameController.getPlayer2().setShooting(true);
                }
                break;
            case W:
                if (gameController.getPlayer2().isAlive()) {
                    gameController.getPlayer2().setAccelerating(true);
                }
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
