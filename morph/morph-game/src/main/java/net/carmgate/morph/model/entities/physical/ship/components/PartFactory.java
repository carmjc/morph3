package net.carmgate.morph.model.entities.physical.ship.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.mgt.MEventManager;

@Singleton
public class PartFactory {

	@Inject private Instance<Part> parts;
	@Inject private MEventManager eventManager;

	private int idGen = 0;

	public <U extends Part> U newInstance(Class<U> clazz) {
		final U u = parts.select(clazz).get();
		eventManager.scanAndRegister(u);
		u.setId(idGen++);
		return u;
	}

}
