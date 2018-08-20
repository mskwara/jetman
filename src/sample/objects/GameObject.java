package sample.objects;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public Node view;
    private Point2D velocity = new Point2D(0, 0);
    private boolean alive = true;
    private boolean turningRight = false;
    private boolean turningLeft = false;
    private double speed;
    private boolean isAccelerating = false;
    private boolean isShooting = false;
    private List<Point2D> multipleMotions = new ArrayList<>();
    private double gravityFactor = 1;
    private double maxGravityFactor = 10;
    private boolean isOnGround = false;

    private Point2D currentVelocity = new Point2D(0, 0);


    GameObject(Node view) {
        this.view = view;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public Point2D getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(Point2D currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    public double getMaxGravityFactor() {
        return maxGravityFactor;
    }

    public void setMaxGravityFactor(double maxGravityFactor) {
        this.maxGravityFactor = maxGravityFactor;
    }

    public double getGravityFactor() {
        return gravityFactor;
    }

    public void setGravityFactor(double gravityFactor) {
        this.gravityFactor = gravityFactor;
    }

    public void changeGravityFactor(double diff) {
        this.gravityFactor += diff;
    }

    public List<Point2D> getMultipleMotions() {
        return multipleMotions;
    }

    public void setMultipleMotions(List<Point2D> multipleMotions) {
        this.multipleMotions = multipleMotions;
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

    public void update() {
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }

    public void updatePosition(Point2D vector) {
        view.setTranslateX(view.getTranslateX() + vector.getX());
        view.setTranslateY(view.getTranslateY() + vector.getY());
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(double x, double y) {
        this.velocity = new Point2D(x, y);
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public Node getView() {
        return view;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getRotate() {
        return view.getRotate();
    }

    public void setTurningRight(boolean bool) {
        this.turningRight = bool;
    }

    public void setTurningLeft(boolean bool) {
        this.turningLeft = bool;
    }

    public boolean isTurningRight() {
        return this.turningRight;
    }

    public boolean isTurningLeft() {
        return this.turningLeft;
    }

    public void rotateRight() {
        //lastRotate = view.getRotate();
        if (view.getRotate() == 363) {
            view.setRotate(3);
        } else {
            view.setRotate(view.getRotate() + 3);
        }
//        System.out.println(view.getRotate());
        if (isAccelerating) {
            setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(speed));
        }
    }

    public void rotateLeft() {
        //lastRotate = view.getRotate();
        if (view.getRotate() == -363) {
            view.setRotate(-3);
        } else {
            view.setRotate(view.getRotate() - 3);
        }
//        System.out.println(view.getRotate());
        if (isAccelerating) {
            setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))).normalize().multiply(speed));
        }
    }

    public boolean isColliding(GameObject other) {
        return getView().getBoundsInParent().intersects(other.getView().getBoundsInParent());
    }

    public boolean hasSpeed() {
        return speed != 0;
    }

    public boolean hasVelocity() {
        return velocity.getX() != 0 || velocity.getY() != 0;
    }

    public double cosRotation() {
        return Math.cos(Math.toRadians(getRotate()));
    }

    public double sinRotation() {
        return Math.sin(Math.toRadians(getRotate()));
    }

    public Point2D createVector() {
        return new Point2D(cosRotation(), sinRotation());
    }

}
