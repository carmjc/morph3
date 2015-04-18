package net.carmgate.morph.model.animations;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnimationFactory {

	@Inject
	private Instance<Animation> animations;

	@SuppressWarnings("unchecked")
	public <U extends Animation> U createAnimation(AnimationType type) {
		return (U) animations.select(type.getClazz()).get();
	}
}
