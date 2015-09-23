package net.carmgate.morph.services.behaviors;

import net.carmgate.morph.model.entities.components.Component;

public abstract class ComponentBehavior<C extends Component> {

	public abstract void eval(C cmp);

	public abstract void init(C cmp);
}
