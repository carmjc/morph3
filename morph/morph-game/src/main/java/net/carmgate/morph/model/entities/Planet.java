package net.carmgate.morph.model.entities;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Planet extends PhysicalEntity {

	@Override
	public BodyDef getBodyDef() {
		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.KINEMATIC;
		return bd;
	}

	@Override
	public FixtureDef getFixtureDef(CircleShape cs) {
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = (float) (2000 / (2 * Math.PI * cs.getRadius()));
		fd.friction = 0;
		fd.restitution = 0;
		return fd;
	}

	@Override
	public CircleShape getShape() {
		CircleShape cs = new CircleShape();
		cs.m_radius = 100;
		return cs;
	}

}
