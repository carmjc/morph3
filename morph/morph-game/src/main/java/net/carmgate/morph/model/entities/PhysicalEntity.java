package net.carmgate.morph.model.entities;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.Renderable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PhysicalEntity implements Renderable {

	@Id private int id;
	private Vector2f pos = new Vector2f();
	private Vector2f speed = new Vector2f();
	private Vector2f accel = new Vector2f();
	private float rotate = 0f;
	private float rotateSpeed = 0f;
	@Transient private final Set<ForceSource> forceSources = Collections.newSetFromMap(new WeakHashMap<>());
	protected float mass;
	private Float rotationTarget;
	private final Matrix4f modelToWorld = new Matrix4f();

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

	public Matrix4f getModelToWorld() {
		return modelToWorld;
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
