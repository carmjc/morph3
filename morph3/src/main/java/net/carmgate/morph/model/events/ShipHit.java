package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class ShipHit extends ShipUpdated {

	private final float damage;

	public ShipHit(Ship ship, float damage) {
		super(ship);
		this.damage = damage;
	}

	public float getDamage() {
		return damage;
	}
}
