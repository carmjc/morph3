package net.carmgate.morph.events;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MEvent<T> {

	@Inject
	private MEventManager eventManager;

	public void fire(T event) {
		eventManager.addEvent(event);
	}
}
