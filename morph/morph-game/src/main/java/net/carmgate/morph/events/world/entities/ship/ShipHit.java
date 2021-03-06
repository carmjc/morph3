package net.carmgate.morph.events.world.entities.ship;

import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;

public class ShipHit extends ShipUpdated {

	private float damage;
	private PhysicalEntity aggressor;

	public PhysicalEntity getAggressor() {
		return aggressor;
	}

	public float getDamage() {
		return damage;
	}

	public void init(PhysicalEntity aggressor, Ship ship, float damage) {
		this.aggressor = aggressor;
		super.setShip(ship);
		this.damage = damage;
	}
}
