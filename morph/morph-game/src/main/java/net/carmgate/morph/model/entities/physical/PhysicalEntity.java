package net.carmgate.morph.model.entities.physical;

import java.util.HashSet;
import java.util.Set;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.Renderable;

public abstract class PhysicalEntity implements Renderable {

	private int id;
	private final Vector2f pos = new Vector2f();
	private final Vector2f speed = new Vector2f();
	private final Vector2f accel = new Vector2f();
	private float rotate = 0f;
	private float rotateSpeed = 0f;
	private final Set<ForceSource> forceSources = new HashSet<>();
	protected float mass;
	private Float rotationTarget;

	public final Vector2f getAccel() {
		return accel;
	}

	public final Set<ForceSource> getForceSources() {
		return forceSources;
	}

	public int getId() {
		return id;
	}

	public final float getMass() {
		return mass;
	}

	public final Vector2f getPos() {
		return pos;
	}

	public float getRotation() {
		return rotate;
	}

	public float getRotationSpeed() {
		return rotateSpeed;
	}

	public Float getRotationTarget() {
		return rotationTarget;
	}

	public final Vector2f getSpeed() {
		return speed;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public void setRotation(float rotate) {
		this.rotate = rotate;
	}

	public void setRotationSpeed(float rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
	}

	public void setRotationTarget(float rotationTarget) {
		this.rotationTarget = rotationTarget;
	}

}
