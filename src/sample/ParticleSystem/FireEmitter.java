package sample.ParticleSystem;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FireEmitter extends Emitter{
    @Override
    public List<Particle> emit(double x, double y){
        List<Particle> particles = new ArrayList<>();

        int numParticles = 2;
        for(int i = 0; i < numParticles ; i++){
            Particle p = new Particle(x, y, new Point2D((Math.random()-0.5) * 0.1, Math.random() * 0.7), 8, 1.0, Color.rgb(191, 191 ,191), BlendMode.ADD);
            particles.add(p);
        }
        return particles;
    }

}
