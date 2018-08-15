package sample;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private Node view;
    private Point2D velocity = new Point2D(0, 0);
    private boolean alive = true;
    private boolean turningRight = false;
    private boolean turningLeft = false;
    private double lastRotate;
    private double speed;
    private boolean isAccelerating = false;
    private boolean isShooting = false;
    private List<Point2D> multipleMotions = new ArrayList<>();

    public List<Point2D> getMultipleMotions() {
        return multipleMotions;
    }


    public double getLastRotate() {
        return lastRotate;
    }

    public void setLastRotate(double lastRotate) {
        this.lastRotate = lastRotate;
    }


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isAccelerating() {
        return isAccelerating;
    }

    public void setAccelerating(boolean accelerating) {
        isAccelerating = accelerating;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public GameObject(Node view){
        this.view = view;
    }
    public void update(){
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }
    public void updateMultipleMotions(Point2D vector){
        view.setTranslateX(view.getTranslateX() + vector.getX());
        view.setTranslateY(view.getTranslateY() + vector.getY());
    }

    public boolean isAlive() {
        return alive;
    }
    public boolean isDead(){
        return !alive;
    }
    public void setVelocity(Point2D velocity){
        this.velocity = velocity;
    }
    public Point2D getVelocity(){
        return velocity;
    }
    public Node getView(){
        return view;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
    }

    public double getRotate(){
        return view.getRotate();
    }
    public void setTurningRight(boolean bool){
        this.turningRight = bool;
    }
    public void setTurningLeft(boolean bool){
        this.turningLeft = bool;
    }

    public boolean isTurningRight(){
        return this.turningRight;
    }
    public boolean isTurningLeft(){
        return this.turningLeft;
    }
    public void rotateRight(){
        //lastRotate = view.getRotate();
        view.setRotate(view.getRotate() + 3);
        if(isAccelerating==true) {
            setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(speed));
        }
    }

    public void rotateLeft(){
        //lastRotate = view.getRotate();
        view.setRotate(view.getRotate() - 3);
        if(isAccelerating==true) {
            setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(speed));
        }
    }
    public boolean isColliding(GameObject other){
        return getView().getBoundsInParent().intersects(other.getView().getBoundsInParent());
    }
}
