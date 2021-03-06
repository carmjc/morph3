package net.carmgate.morph.ui.actions;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.MWorld.TimeFreezeCause;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.UIContext.Context;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class ToggleEditor implements KeyboardListener {

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private UIContext uiContext;
	@Inject private InputHistory inputHistory;
	@Inject private MWorld world;

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent(1).getButton() == conf.getCharProperty("action.enterShipEditor.key")
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP
				&& uiContext.getSelectedShip() != null) {
			uiContext.setContext(Context.SHIP_EDITOR);
			if (!world.isTimeFrozen()) {
				world.toggleTimeFrozen(TimeFreezeCause.SHIP_EDITOR);
			}
			LOGGER.debug("Entering ship editor mode");
		}

		if (inputHistory.getLastKeyboardEvent(1).getButton() == conf.getCharProperty("action.leaveShipEditor.key")
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP
				&& uiContext.getContext() == Context.SHIP_EDITOR) {
			uiContext.setContext(Context.GAME);
			if (world.getTimeFreezeCause() == TimeFreezeCause.SHIP_EDITOR) {
				world.toggleTimeFrozen(TimeFreezeCause.SHIP_EDITOR);
			}
			LOGGER.debug("Leaving ship editor mode");
		}

	}
}