package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class Save implements KeyboardListener {

	@Inject private Logger LOGGER;
	@Inject private KeyboardManager keyboardManager;
	@Inject private InputHistory inputHistory;
	@Inject private EntityManager entityManager;
	@Inject private World world;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		keyboardManager.addKeyboardListener(this);
	}

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent().getKey() == Keyboard.KEY_F5 && inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP) {
			entityManager.getTransaction().begin();
			World tmpWorld = entityManager.find(World.class, 1);
			if (tmpWorld != null) {
				entityManager.remove(tmpWorld);
			}
			entityManager.getTransaction().commit();
			entityManager.getTransaction().begin();
			entityManager.persist(world);
			entityManager.getTransaction().commit();
		}
	}

}
