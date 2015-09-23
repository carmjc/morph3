package net.carmgate.morph.model.animations;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;

@Singleton
public class AnimationFactory {

	@Inject
	private Instance<Animation> animations;

	@Inject
	private MEventManager eventManager;

	@SuppressWarnings("unchecked")
	public <U extends Animation> U newInstance(Class<U> clazz) {
		U u = animations.select(clazz).get();
		eventManager.scanAndRegister(u);
		return u;
	}
}
