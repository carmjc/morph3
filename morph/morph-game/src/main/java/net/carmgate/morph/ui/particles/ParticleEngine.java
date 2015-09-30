package net.carmgate.morph.ui.particles;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.model.World;

@Singleton
public class ParticleEngine {

	@Inject private Logger LOGGER;
	@Inject private World world;

	private final List<Particle> bgParticles = new ArrayList<>();
	private final List<Particle> fgParticles = new ArrayList<>();
	private final List<Particle> particles = new ArrayList<>();
	private final List<Particle> particlesToRemove = new ArrayList<>();
	private final List<ParticleSource> sources = new ArrayList<>();

	private void addParticle(Particle particle) {
		particles.add(particle);
		if (particle.isBg()) {
			bgParticles.add(particle);
		} else {
			fgParticles.add(particle);
		}
	}

	public void addParticleSource(ParticleSource pSource) {
		sources.add(pSource);
	}

	public List<Particle> getBgParticles() {
		return bgParticles;
	}

	public List<Particle> getFgParticles() {
		return fgParticles;
	}

	private void removeParticle(Particle p) {
		particles.remove(p);
		if (p.isBg()) {
			bgParticles.remove(p);
		} else {
			fgParticles.remove(p);
		}
	}

	public void removeParticleSource(ParticleSource pSource) {
		sources.remove(pSource);
	}

	public void update() {
		if (world.isTimeFrozen()) {
			return;
		}

		long worldTime = world.getTime();

		// generate new particles
		for (ParticleSource ps : sources) {
			float nbParticles = ps.getBirthRate() * world.getMillisSinceLastUpdate() / 1000;
			for (int i = 0; i < nbParticles; i++) {
				Particle p = ps.createParticle();
				if (p != null) {
					addParticle(p);
				}
			}
		}

		// remove dead particles and compute kinematics
		for (Particle p : particles) {
			if (p.getDeathTime() < worldTime) {
				particlesToRemove.add(p);
			}

			p.getSpeed().x += p.getAccel().x * world.getMillisSinceLastUpdate() / 1000;
			p.getSpeed().y += p.getAccel().y * world.getMillisSinceLastUpdate() / 1000;
			p.getPos().x += p.getSpeed().x * world.getMillisSinceLastUpdate() / 1000;
			p.getPos().y += p.getSpeed().y * world.getMillisSinceLastUpdate() / 1000;
		}

		if (particlesToRemove.size() > 0) {
			for (Particle p : particlesToRemove) {
				removeParticle(p);
			}
			particlesToRemove.clear();
		}
	}
}
