package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Pane root;
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private GameObject player;
    private double gravity = 1, gravityFactor=1;

    private Parent createContent(){
        root = new Pane();
        root.setPrefSize(900, 900);

        player = new Player();
        //player.setVelocity(new Point2D(1,0));

        addGameObject(player, 300, 300);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }
    private void addBullet(GameObject bullet, double x, double y){
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }
    private void addEnemy(GameObject enemy, double x, double y){
        enemies.add(enemy);
        addGameObject(enemy, x, y);
    }
    private void addGameObject(GameObject object, double x, double y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate(){
        player.getView().setTranslateY(player.getView().getTranslateY()+gravity*gravityFactor);

        for(GameObject bullet : bullets){
            for(GameObject enemy : enemies){
                if(bullet.isColliding(enemy)){
                    bullet.setAlive(false);
                    enemy.setAlive(false);

                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }
        }

        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);

        player.update();
        if(player.isAccelerating()==true){
            if(gravityFactor>1)     gravityFactor-=0.07;
            else    gravityFactor = 1;

            if((player.getVelocity().getX()==0 && player.getVelocity().getY()==0)) { //statek nie porusza sie
                player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                player.setSpeed(1);
                System.out.println("1");
            }
            else { // statek porusza się
                if(player.getSpeed()<=9 && player.getSpeed()>=1) { //statek ma prędkość niemaksymalną
                    if(helper.round(player.getVelocity().getX()) != helper.round(Math.cos(Math.toRadians(player.getRotate()))*player.getSpeed())
                            || helper.round(player.getVelocity().getY()) != helper.round(Math.sin(Math.toRadians(player.getRotate()))*player.getSpeed())){

                        player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                                Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(player.getSpeed())); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
                        player.setSpeed(player.getSpeed());
                        System.out.println("2\n"+player.getVelocity().getX()+"\n"+Math.cos(Math.toRadians(player.getRotate()))*player.getSpeed());
                    }

                    else {
                        player.setVelocity(new Point2D(player.getVelocity().getX() * 1.07, player.getVelocity().getY() * 1.07));
                        player.setSpeed(player.getSpeed() * 1.07);
                        System.out.println("3");
                    }
                }
                else if(player.getSpeed()<1){
                    player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                    player.setSpeed(1);
                    System.out.println("4");
                }
                else if(player.getSpeed()>9) {  //gdy osiągnie max prędkość
                    player.setVelocity(new Point2D(player.getVelocity().getX(), player.getVelocity().getY()));
                    System.out.println("5");
                }
            }
        } else if(player.isAccelerating()==false){
            if(gravityFactor<5)     gravityFactor+=0.07;
            else    gravityFactor = 5;

            /*if(player.getSpeed()>=0.04) {
                player.setVelocity(new Point2D(player.getVelocity().getX() / 1.07, player.getVelocity().getY() / 1.07));
                player.setSpeed(player.getSpeed() / 1.07);
            }
            else {
                player.setVelocity(new Point2D(0, 0));
                player.setSpeed(0);
            }*/
        }

        if(player.isTurningLeft()){
            player.rotateLeft();
        }   else if(player.isTurningRight()){
            player.rotateRight();
        }
        if(player.isShooting()==true){
            shot();
        }

        //początek ruchu bezwładnościowego
        int i = 0;
        for(Point2D vector : player.getMultipleMotions()){
            if(Math.abs(vector.getX()) >= 0.04 || Math.abs(vector.getY()) >= 0.04) {
                player.updateMultipleMotions(vector);
                Point2D newVector = new Point2D(vector.getX() * 0.98, vector.getY() * 0.98);
                player.getMultipleMotions().set(i, newVector);
                System.out.println("nowy: " + player.getMultipleMotions().get(i));
            }
            else {
                Point2D newVector = new Point2D(0, 0);
                player.getMultipleMotions().set(i, newVector);
            }
            i++;
        }
//koniec bezwładności

        if(Math.random() < 0.02){
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    private static class Player extends GameObject {
        Player(){
            super(new Rectangle(40, 20, Color.BLUE));
        }
    }
    private static class Enemy extends GameObject {
        Enemy(){
            super(new Circle(15, 15, 15, Color.RED));
        }
    }
    private static class Bullet extends GameObject {
        Bullet(){
            super(new Circle(5, 5, 5, Color.BROWN));
        }
    }
    private void shot(){
        Bullet bullet = new Bullet();
        if(player.getVelocity().getX()!=0 && player.getVelocity().getY()!=0) { //jeśli statek się porusza
            if(player.getVelocity().getX() != Math.cos(Math.toRadians(player.getRotate())) || player.getVelocity().getY()!=Math.sin(Math.toRadians(player.getRotate()))) {
                bullet.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                        Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(15)); //ten if jest gdy statek zwalnia, a gracz go obróci i zacznie lecieć w inną stronę
            }
            else {
                bullet.setVelocity(player.getVelocity().normalize().multiply(15));
            }
        }
        else{
            bullet.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))).normalize().multiply(15));
        }
        addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
    }

    @Override
    public void start(Stage Stage) throws Exception{
        Stage.setTitle("Hello World");
        Stage.setScene(new Scene(createContent()));
        Stage.getScene().setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.LEFT){
                player.setTurningLeft(true);
            } else if(e.getCode() == KeyCode.RIGHT){
                player.setTurningRight(true);
            } else if(e.getCode() == KeyCode.ENTER){
                player.setShooting(true);
            } else if(e.getCode() == KeyCode.UP){
                player.setAccelerating(true);
            }
        });

        Stage.getScene().setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.LEFT){
                player.setTurningLeft(false);
            } else if(e.getCode() == KeyCode.RIGHT){
                player.setTurningRight(false);
            } else if(e.getCode() == KeyCode.ENTER){
                player.setShooting(false);
            }else if(e.getCode() == KeyCode.UP){
                Point2D vector = new Point2D(player.getVelocity().getX(), player.getVelocity().getY());
                player.getMultipleMotions().add(vector);
                for(int i = 0 ; i<player.getMultipleMotions().size() ; i++){
                    System.out.println(player.getMultipleMotions().get(i));
                }
                Point2D velo = new Point2D(0,0);
                player.setVelocity(velo);
                player.setAccelerating(false);
            }
        });
        Stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
