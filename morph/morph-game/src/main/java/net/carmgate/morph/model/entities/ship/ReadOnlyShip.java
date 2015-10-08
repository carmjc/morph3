package net.carmgate.morph.model.entities.ship;

import org.jbox2d.common.Vec2;

import net.carmgate.morph.model.entities.PhysicalEntity;

/**
 * This class is used to provide the script writers a safe clone of the ship to use
 */
public class ReadOnlyShip {

	private PhysicalEntity ship;

	public ReadOnlyShip(PhysicalEntity ship) {
		this.ship = ship;
	}

	public final Vec2 getPos() {
		return new Vec2(ship.getPosition());
	}

	public final Vec2 getSpeed() {
		return new Vec2(ship.getBody().getLinearVelocity());
	}

}
