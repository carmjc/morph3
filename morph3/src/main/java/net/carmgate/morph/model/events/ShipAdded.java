package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class ShipAdded implements WorldEvent {
	private final Ship ship;

	public ShipAdded(Ship ship) {
		this.ship = ship;
	}

	public Ship getShip() {
		return ship;
	}

}
