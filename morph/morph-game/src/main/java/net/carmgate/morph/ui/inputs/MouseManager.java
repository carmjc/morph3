package net.carmgate.morph.ui.inputs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.input.Mouse;
import org.slf4j.Logger;

import net.carmgate.morph.ui.inputs.UIEvent.EventType;

@Singleton
public class MouseManager {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private GameMouse gameMouse;

	private final List<MouseListener> mouseListeners = new ArrayList<>();

	public void addMouseListener(MouseListener mouseListener) {
		mouseListeners.add(mouseListener);
		LOGGER.debug("Added new mouse listener: " + mouseListener.getClass().getName()); //$NON-NLS-1$
	}

	public void handleMouseEvent() {
		if (Mouse.next()) {
			final int dWheel = Mouse.getDWheel();
			if (dWheel != 0) {
				LOGGER.debug("Logged a mouse wheel: " + dWheel); //$NON-NLS-1$
				final UIEvent event = new UIEvent(EventType.MOUSE_WHEEL, dWheel, new int[] { Mouse.getEventX(), Mouse.getEventY() });
				inputHistory.addEvent(event);
			}

			// add interaction to ui context
			EventType evtType = null;
			if (Mouse.getEventButton() >= 0) {
				if (Mouse.getEventButtonState()) {
					evtType = EventType.MOUSE_BUTTON_DOWN;
				} else {
					evtType = EventType.MOUSE_BUTTON_UP;
				}
				final UIEvent event = new UIEvent(evtType, Mouse.getEventButton(), new int[] { Mouse.getEventX(), Mouse.getEventY() });
				inputHistory.addEvent(event);
			}

			final int dx = Mouse.getDX();
			final int dy = Mouse.getDY();
			if (dx != 0 || dy != 0) {
				if (inputHistory.getLastMouseEvent().getEventType() != EventType.MOUSE_MOVE) {
					final UIEvent event = new UIEvent(EventType.MOUSE_MOVE, Mouse.getEventButton(), new int[] { gameMouse.getX(), gameMouse.getY() });
					inputHistory.addEvent(event);
				}
			}

			for (final MouseListener mouseListener : mouseListeners) {
				mouseListener.onMouseEvent();
			}

			// Try to clean input history
			if (inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
					&& inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP) {
				inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
			}
		}
	}

	public void removeMouseListener(MouseListener mouseListener) {
		mouseListeners.remove(mouseListener);
	}
}
