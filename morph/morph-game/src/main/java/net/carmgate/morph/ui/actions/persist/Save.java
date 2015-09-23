package net.carmgate.morph.ui.actions.persist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class Save implements KeyboardListener {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private EntityManager entityManager;
	@Inject private World world;

	// TODO Does not work if world has been initialized from DB
	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent().getKey() == Keyboard.KEY_F5 && inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP) {
			entityManager.getTransaction().begin();
			LOGGER.debug("Trying to remove");
			World tmpWorld = entityManager.find(World.class, 1);
			if (tmpWorld != null) {
				entityManager.remove(tmpWorld);
				entityManager.detach(tmpWorld);
			}
			LOGGER.debug("Trying to persist");
			entityManager.persist(world);
			entityManager.getTransaction().commit();
		}
	}

}
