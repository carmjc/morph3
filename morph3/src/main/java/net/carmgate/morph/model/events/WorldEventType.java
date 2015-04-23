package net.carmgate.morph.model.events;

import net.carmgate.morph.model.events.animations.AnimationStart;
import net.carmgate.morph.model.events.entities.ship.ShipAdded;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.events.entities.ship.ShipHit;

public enum WorldEventType {
	ANIMATION_START(AnimationStart.class),
	SHIP_ADDED(ShipAdded.class),
	SHIP_DEATH(ShipDeath.class),
	SHIP_HIT(ShipHit.class);

	private final Class<? extends WorldEvent> clazz;

	WorldEventType(Class<? extends WorldEvent> clazz) {
		this.clazz = clazz;

	}

	public Class<? extends WorldEvent> getClazz() {
		return clazz;
	}
}
