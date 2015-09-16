package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.World.TimeFreezeCause;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.UIContext.Context;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class TogglePause implements KeyboardListener {

	@Inject private Logger LOGGER;
	@Inject private KeyboardManager keyboardManager;
	@Inject private InputHistory inputHistory;
	@Inject private World world;
	@Inject private Conf conf;
	@Inject private UIContext uiContext;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		keyboardManager.addKeyboardListener(this);
	}

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent(1).getButton() == conf.getCharProperty("action.pauseTime.key")
				&& inputHistory.getLastKeyboardEvent(1).getEventType() == EventType.KEYBOARD_DOWN
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP
				&& uiContext.getContext() == Context.GAME) {
			if (!world.isTimeFrozen() || world.getTimeFreezeCause() == TimeFreezeCause.PAUSE_ACTION) {
				world.toggleTimeFrozen(TimeFreezeCause.PAUSE_ACTION);
			}
			inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
		}
	}

}
