package net.carmgate.morph.events.world.entities.ship;

import net.carmgate.morph.events.world.WorldEvent;
import net.carmgate.morph.model.entities.ship.Ship;

public abstract class ShipUpdated implements WorldEvent {

	private Ship ship;

	public Ship getShip() {
		return ship;
	}

	protected void setShip(Ship ship) {
		this.ship = ship;
	}
}
