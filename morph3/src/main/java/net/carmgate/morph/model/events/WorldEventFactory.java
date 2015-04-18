package net.carmgate.morph.model.events;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldEventFactory {

	@Inject
	Instance<WorldEvent> worldEventInstances;

	@SuppressWarnings("unchecked")
	public <U extends WorldEvent> U createWorldEvent(WorldEventType type) {
		return (U) worldEventInstances.select(type.getClazz()).get();
	}

}
