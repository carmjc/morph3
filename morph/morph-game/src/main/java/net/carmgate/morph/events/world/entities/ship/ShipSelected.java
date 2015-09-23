package net.carmgate.morph.events.world.entities.ship;

import net.carmgate.morph.events.MEvent;
import net.carmgate.morph.model.entities.ship.Ship;

public class ShipSelected extends MEvent {
	private Ship ship;

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}
}
