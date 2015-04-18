package net.carmgate.morph.model.entities.physical;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.eventmgt.EventManager;

@Singleton
public class PhysicalEntityFactory {

	@Inject
	private Instance<PhysicalEntity> physicalEntities;

	@Inject
	private EventManager eventManager;

	@SuppressWarnings("unchecked")
	public <U extends PhysicalEntity> U createEntity(PhysicalEntityType type) {
		final U u = (U) physicalEntities.select(type.getClazz()).get();
		eventManager.scan(u);
		return u;
	}

}
