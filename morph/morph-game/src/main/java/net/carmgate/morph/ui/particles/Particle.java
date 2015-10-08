package net.carmgate.morph.ui.particles;

import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.ui.renderers.Renderable;

public class Particle implements Renderable {

	private boolean bg;
	private Vec2 pos = new Vec2();
	private Vec2 speed = new Vec2();
	private Vec2 accel = new Vec2();
	private float rotation;
	private long deathTime;
	private long birthTime;

	public Vec2 getAccel() {
		return accel;
	}

	public long getBirthTime() {
		return birthTime;
	}

	public long getDeathTime() {
		return deathTime;
	}

	public Vec2 getPos() {
		return pos;
	}

	public float getRotation() {
		return rotation;
	}

	public Vec2 getSpeed() {
		return speed;
	}

	public boolean isBg() {
		return bg;
	}

	public void setAccel(Vec2 accel) {
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

	public void setPos(Vec2 pos) {
		this.pos = pos;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setSpeed(Vec2 speed) {
		this.speed = speed;
	}
}
