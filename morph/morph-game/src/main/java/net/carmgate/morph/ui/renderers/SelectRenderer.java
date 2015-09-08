package net.carmgate.morph.ui.renderers;


public interface SelectRenderer<T extends Renderable> extends Renderer<T> {

	public static enum TargetType {
		SHIP,
		NON_SHIP_PHYSICAL_ENTITY,
		WIDGET,
		COMPONENT;
	}
}
