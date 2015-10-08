package net.carmgate.morph.model.entities;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.Renderable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PhysicalEntity implements Renderable {

	@Id private int id;
	// private Vector2f pos = new Vector2f();
	// private Vector2f speed = new Vector2f();
	// private Vector2f accel = new Vector2f();
	// private float rotate = 0f;
	// private float rotateSpeed = 0f;
	@Transient private final Set<ForceSource> forceSources = Collections.newSetFromMap(new WeakHashMap<>());
	// protected float mass;
	// private Float rotationTarget;
	private final Matrix4f modelToWorld = new Matrix4f();
	@Transient private Body body;

	public Body getBody() {
		return body;
	}

	public abstract BodyDef getBodyDef();

	public abstract FixtureDef getFixtureDef(CircleShape cs);

	public final Set<ForceSource> getForceSources() {
		return forceSources;
	}

	public int getId() {
		return id;
	}

	public Matrix4f getModelToWorld() {
		return modelToWorld;
	}

	public final Vec2 getPosition() {
		return body.getPosition().mul(1000);
	}

	public abstract CircleShape getShape();

	public void setBody(Body body) {
		this.body = body;
	}

	public void setId(int id) {
		this.id = id;
	}

}
