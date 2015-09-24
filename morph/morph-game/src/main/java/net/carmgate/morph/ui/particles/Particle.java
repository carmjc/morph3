package net.carmgate.morph.ui.particles;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.renderers.Renderable;

public class Particle implements Renderable {

	private boolean bg;
	private Vector2f pos = new Vector2f();
	private Vector2f speed = new Vector2f();
	private Vector2f accel = new Vector2f();
	private float rotation;
	private long deathTime;
	private long birthTime;

	public Vector2f getAccel() {
		return accel;
	}

	public long getBirthTime() {
		return birthTime;
	}

	public long getDeathTime() {
		return deathTime;
	}

	public Vector2f getPos() {
		return pos;
	}

	public float getRotation() {
		return rotation;
	}

	public Vector2f getSpeed() {
		return speed;
	}

	public boolean isBg() {
		return bg;
	}

	public void setAccel(Vector2f accel) {
		this.accel = accel;
	}

	public void setBg(boolean bg) {
		this.bg = bg;
	}

	public void setBirthTime(long birthTime) {
		this.birthTime = birthTime;
	}

	public void setDeathTime(long deathTime) {
		this.deathTime = deathTime;
	}

	public void setPos(Vector2f pos) {
		this.pos = pos;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setSpeed(Vector2f speed) {
		this.speed = speed;
	}
}
