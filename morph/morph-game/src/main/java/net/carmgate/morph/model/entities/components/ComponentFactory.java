package net.carmgate.morph.model.entities.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.services.ComponentManager;

@Singleton
public class ComponentFactory {

	@Inject private Instance<Component> componentInstances;
	@Inject private MEventManager eventManager;
	@Inject private ComponentManager componentManager;

	private int idGen = 0;

	public <U extends Component> Component newInstance(Class<U> clazz) {
		final U u = componentInstances.select(clazz).get();
		eventManager.scanAndRegister(u);
		u.setId(idGen++);
		componentManager.init(u);
		return u;
	}

}
