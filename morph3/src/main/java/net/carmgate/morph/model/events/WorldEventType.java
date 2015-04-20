package net.carmgate.morph.model.events;

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