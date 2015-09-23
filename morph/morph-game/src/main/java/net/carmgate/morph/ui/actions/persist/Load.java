package net.carmgate.morph.ui.actions.persist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class Load implements KeyboardListener {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private EntityManager entityManager;
	@Inject private World world;
	@Inject private UIContext uiContext;

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent().getKey() == Keyboard.KEY_F9 && inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_DOWN) {
			// Reset world
			world.getPhysicalEntities().removeAll(world.getShips());
			world.getShips().clear();

			entityManager.getTransaction().begin();
			World newWorld = entityManager.find(World.class, 1);
			entityManager.detach(newWorld);
			entityManager.getTransaction().commit();

			world.loadFrom(newWorld);
			world.resetLastUpdateTime();

			// Resets context depending on reloaded elements
			uiContext.setSelectedCmp(null);
			uiContext.setSelectedShip(null);
		}
	}

}
