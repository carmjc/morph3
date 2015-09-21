package net.carmgate.morph.model.entities.parts;

import net.carmgate.morph.model.entities.components.Component;

public abstract class Part<C extends Component> {

	private int id;
	private int level = 0;
	private Component component;

	public Part() {
		super();
	}

	public abstract void computeEffectOnComponent(C cmp);

	public Component getComponent() {
		return component;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public abstract int getXpNeededForNextLevel();

	public void setComponent(Component component) {
		this.component = component;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}