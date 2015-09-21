package net.carmgate.morph.model.entities.components;

public interface Activable {

	default void evalBehavior() {
	};

	boolean isActive();

	void setActive(boolean active);

	default void startBehavior() {
	};

}