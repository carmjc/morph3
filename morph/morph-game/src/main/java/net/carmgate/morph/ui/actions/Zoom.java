package net.carmgate.morph.ui.actions;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

public class Zoom implements MouseListener {

	private static float ZOOM_VARIATION;
	private static float ZOOM_MAX;

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;

	@Inject private GameMouse gameMouse;
	@Inject private Conf conf;

	@Override
	public void onMouseEvent() {
		while (inputHistory.getLastMouseEvent().getButton() == Keyboard.KEY_UP
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.KEYBOARD_UP
				|| inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_WHEEL
				&& inputHistory.getLastMouseEvent().getButton() > 0) {

			ViewPort viewport = uiContext.getViewport();

			// Correct max zoom level
			float zoomVariation = ZOOM_VARIATION;
			if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
				zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
			}

			Vec2 fromWindowCenterToMouse = new Vec2(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
					-uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
			uiContext.getViewport().getFocalPoint().sub(new Vec2(fromWindowCenterToMouse).scale(1f / viewport.getZoomFactor()))
			.add(new Vec2(fromWindowCenterToMouse).scale(1f / (viewport.getZoomFactor() * zoomVariation)));
			viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}

		while (inputHistory.getLastMouseEvent().getButton() == Keyboard.KEY_DOWN
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.KEYBOARD_UP
				|| inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_WHEEL
				&& inputHistory.getLastMouseEvent().getButton() < 0) {

			ViewPort viewport = uiContext.getViewport();

			// Correct max zoom level
			float zoomVariation = 1 / ZOOM_VARIATION;
			if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
				zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
			}

			Vec2 fromWindowCenterToMouse = new Vec2(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
					-uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
			uiContext.getViewport().getFocalPoint().sub(new Vec2(fromWindowCenterToMouse).scale(1f / viewport.getZoomFactor()))
			.add(new Vec2(fromWindowCenterToMouse).scale(1f / (viewport.getZoomFactor() * zoomVariation)));
			viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
	}

	@PostConstruct
	private void postConstruct() {
		ZOOM_VARIATION = conf.getFloatProperty("zoom.variationFactor"); //$NON-NLS-1$
		ZOOM_MAX = conf.getFloatProperty("zoom.max"); //$NON-NLS-1$
	}
}
