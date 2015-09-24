package net.carmgate.morph.model.entities;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.services.ShipManager;

@Singleton
public class PhysicalEntityFactory {

	@Inject private Instance<PhysicalEntity> physicalEntities;
	@Inject private MEventManager eventManager;
	@Inject private ShipManager shipManager;

	private int idGen = 0;

	@SuppressWarnings("unchecked")
	public <U extends PhysicalEntity> U newInstance(Class<U> clazz) {
		final U u = physicalEntities.select(clazz).get();
		u.setId(idGen++);
		eventManager.scanAndRegister(u);
		return u;
	}

}
