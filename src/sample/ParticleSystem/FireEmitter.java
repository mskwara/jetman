package sample.ParticleSystem;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import sample.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class FireEmitter extends Emitter{
    @Override
    public List<Particle> emit(double x, double y, GameObject bullet){
        List<Particle> particles = new ArrayList<>();

        int numParticles = 1;
        for(int i = 0; i < numParticles ; i++){
            Particle p = new Particle(x, y, new Point2D((Math.random()-0.5) * 2, Math.random() * 2), 12, 0.5, Color.rgb(191, 191 ,191), BlendMode.ADD);
            p.setEmitter(bullet);
            particles.add(p);
        }
        return particles;
    }

}
