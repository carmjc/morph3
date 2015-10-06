package net.carmgate.morph.model.animations;

import net.carmgate.morph.model.entities.components.Component;

public class ComponentAnimation extends Animation {

	private Component sourceComponent;

	public Component getSource() {
		return sourceComponent;
	}

	public void setSource(Component sourceComponent) {
		this.sourceComponent = sourceComponent;
	}

}
