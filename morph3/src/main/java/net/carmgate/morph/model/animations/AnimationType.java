package net.carmgate.morph.model.animations;

public enum AnimationType {
	LASER(Laser.class);

	private final Class<? extends Animation> clazz;

	private AnimationType(Class<? extends Animation> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends Animation> getClazz() {
		return clazz;
	}
}
