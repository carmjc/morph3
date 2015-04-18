package net.carmgate.morph.model.entities.physical;

public enum PhysicalEntityType {
	SHIP(Ship.class);

	private final Class<? extends PhysicalEntity> clazz;

	private PhysicalEntityType(Class<? extends PhysicalEntity> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends PhysicalEntity> getClazz() {
		return clazz;
	}
}
