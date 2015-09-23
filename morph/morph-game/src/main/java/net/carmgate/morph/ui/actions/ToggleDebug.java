package net.carmgate.morph.ui.actions;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.RenderMode;

public class ToggleDebug implements KeyboardListener {

	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;
	@Inject private Conf conf;

	@Override
	public void onKeyboardEvent() {
		if (inputHistory.getLastKeyboardEvent(1).getButton() == conf.getCharProperty("action.toggleDebug.key")
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP) {
			uiContext.setRenderMode(uiContext.getRenderMode() == RenderMode.DEBUG ? RenderMode.NORMAL : RenderMode.DEBUG);
			inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
		}
		if (inputHistory.getLastKeyboardEvent().getButton() == conf.getCharProperty("action.toggleSelectDebug.key") //$NON-NLS-1$
				&& (uiContext.getRenderMode() == RenderMode.DEBUG || uiContext.getRenderMode() == RenderMode.SELECT_DEBUG)
				&& inputHistory.getLastKeyboardEvent().getEventType() == EventType.KEYBOARD_UP) {
			uiContext.setRenderMode(uiContext.getRenderMode() == RenderMode.SELECT_DEBUG ? RenderMode.DEBUG : RenderMode.SELECT_DEBUG);
			inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
		}
	}

}
