package net.carmgate.morph.ui.particles;

public abstract class ParticleSource {

	private float birthRate;

	public abstract Particle createParticle();

	public float getBirthRate() {
		return birthRate;
	}

	public void setBirthRate(float birthRate) {
		this.birthRate = birthRate;
	}

}
