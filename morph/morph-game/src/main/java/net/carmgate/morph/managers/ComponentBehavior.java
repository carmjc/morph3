package net.carmgate.morph.managers;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.environment.se.events.ContainerInitialized;

import net.carmgate.morph.model.entities.components.Component;

public abstract class ComponentBehavior<C extends Component> {

	@Inject private ComponentManager componentManager;

	public abstract void eval(C cmp);

	public abstract void init(C cmp);

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		componentManager.addComponentEvaluator(this);
	}

}
