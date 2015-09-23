package net.carmgate.morph.events.world;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;

@Singleton
public class WorldEventFactory {

	@Inject
	Instance<WorldEvent> worldEventInstances;

	@Inject
	private MEventManager eventManager;

	@SuppressWarnings("unchecked")
	public <U extends WorldEvent> U newInstance(Class<U> clazz) {
		U u = worldEventInstances.select(clazz).get();
		eventManager.scanAndRegister(u);
		return u;
	}

}
