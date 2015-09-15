package net.carmgate.morph.model.animations;

import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.model.animations.ship.MiningLaserAnim;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;

public enum AnimationType {
	LASER(LaserAnim.class),
	MINING_LASER(MiningLaserAnim.class),
	XP_AWARDED(XpAwardedAnimation.class);

	private final Class<? extends Animation> clazz;

	private AnimationType(Class<? extends Animation> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends Animation> getClazz() {
		return clazz;
	}
}
