package net.carmgate.morph.events.entities.component;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.entities.components.Component;

public class ComponentLoaded implements WorldEvent {
	private final Component component;

	public ComponentLoaded(Component component) {
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}
}
