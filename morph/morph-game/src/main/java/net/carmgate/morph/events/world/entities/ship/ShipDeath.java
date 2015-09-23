package net.carmgate.morph.events.world.entities.ship;

import net.carmgate.morph.model.entities.ship.Ship;

public class ShipDeath extends ShipUpdated {

	public void setDeadShip(Ship ship) {
		super.setShip(ship);
	}

}
