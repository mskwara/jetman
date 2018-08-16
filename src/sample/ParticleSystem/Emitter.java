package sample.ParticleSystem;

import java.util.List;

public abstract class Emitter {
    public abstract List<Particle> emit(double x, double y);
}
