package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.Ship;

public class ShipDead extends ShipUpdated {

	public ShipDead(Ship ship) {
		super(ship);
	}

}
