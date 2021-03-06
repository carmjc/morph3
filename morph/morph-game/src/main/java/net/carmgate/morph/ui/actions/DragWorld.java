package net.carmgate.morph.ui.actions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.common.Vec2;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.DragContext;
import net.carmgate.morph.ui.inputs.DragContext.DragType;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

@Singleton
public class DragWorld implements MouseListener {

	@Inject private org.slf4j.Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;
	@Inject private GameMouse gameMouse;
	@Inject private DragContext dragContext;

	@Override
	public void onMouseEvent() {
		// if (uiContext.getSelectedWidget() != null) {
		// return;
		// }

		if (inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 0
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				/* && !dragContext.dragInProgress() */) {

			if (gameMouse.pick() != null) {
				LOGGER.debug("Got a mouse pick: " + gameMouse.pick().getUserData());
				return;
			}

			dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
			dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());
			dragContext.setDragType(DragType.WORLD);

			final Vec2 oldFP = dragContext.getOldFP();
			final Vec2 oldMousePosInWindow = dragContext.getOldMousePosInWindow();
			if (oldFP != null) {
				final ViewPort viewport = uiContext.getViewport();
				final Vec2 fp = viewport.getFocalPoint();
				fp.x = (oldFP.x * uiContext.getViewport().getZoomFactor() - (gameMouse.getX() - oldMousePosInWindow.x)) / uiContext.getViewport().getZoomFactor();
				fp.y = (oldFP.y * uiContext.getViewport().getZoomFactor() + (gameMouse.getY() - oldMousePosInWindow.y)) / uiContext.getViewport().getZoomFactor();
			}

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
		}

		while (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& dragContext.dragInProgress(DragType.WORLD)) {

			final Vec2 oldFP = dragContext.getOldFP();
			final Vec2 oldMousePosInWindow = dragContext.getOldMousePosInWindow();
			if (oldFP != null) {
				final ViewPort viewport = uiContext.getViewport();
				final Vec2 fp = viewport.getFocalPoint();
				fp.x = (oldFP.x * uiContext.getViewport().getZoomFactor() - (gameMouse.getX() - oldMousePosInWindow.x)) / uiContext.getViewport().getZoomFactor();
				fp.y = (oldFP.y * uiContext.getViewport().getZoomFactor() + (gameMouse.getY() - oldMousePosInWindow.y)) / uiContext.getViewport().getZoomFactor();
			}

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
		if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& dragContext.dragInProgress(DragType.WORLD)) {
			dragContext.reset();
			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
	}
}
