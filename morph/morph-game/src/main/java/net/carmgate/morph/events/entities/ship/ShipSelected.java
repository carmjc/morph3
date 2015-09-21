package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.events.mgt.MEvent;
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
