package net.carmgate.morph.model.orders;

import net.carmgate.morph.model.orders.ship.Attack;
import net.carmgate.morph.model.orders.ship.Flee;

public enum OrderType {
	ATTACK(Attack.class),
	FLEE(Flee.class);

	private final Class<? extends Order> clazz;

	OrderType(Class<? extends Order> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends Order> getClazz() {
		return clazz;
	}
}
