package net.carmgate.morph.model.orders.ship;

import net.carmgate.morph.model.entities.Laser;
import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.orders.Order;

public class Attack extends Order {

	private final Ship source;
	private final Ship target;

	public Attack(Ship source, Ship target) {
		this.source = source;
		this.target = target;
	}

	@Override
	protected void evaluate() {
		final Laser laser = new Laser(source, target);
		getWorld().add(laser);

		target.fireShipUpdate(new ShipHit(target, 1));

		setNextEvalTime(getNextEvalTime() + 1000);
	}

}
