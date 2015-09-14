package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class PauseTime implements KeyboardListener {

	@Inject private KeyboardManager keyboardManager;
	@Inject private InputHistory inputHistory;
	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		keyboardManager.addKeyboardListener(this);
	}

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent(1).getButton() == conf.getCharProperty("action.pauseTime.key")
				&& inputHistory.getLastKeyboardEvent(1).getEventType() == EventType.KEYBOARD_DOWN
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP) {
			world.toggleTimeFrozen();
			LOGGER.debug("Freeze");
			inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
		}
	}

}
