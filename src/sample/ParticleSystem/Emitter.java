package sample.ParticleSystem;

import sample.objects.GameObject;

import java.util.List;

public abstract class Emitter {
    public abstract List<Particle> emit(double x, double y, GameObject bullet);
}
