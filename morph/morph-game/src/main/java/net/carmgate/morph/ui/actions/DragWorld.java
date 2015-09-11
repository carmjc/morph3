package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Mouse;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.actions.DragContext.DragType;
import net.carmgate.morph.ui.actions.Select.PickingResult;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

@Singleton
public class DragWorld implements MouseListener {

	@Inject private org.slf4j.Logger LOGGER;
	@Inject private MouseManager mouseManager;
	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;
	@Inject private GameMouse gameMouse;
	@Inject private DragContext dragContext;
	@Inject private Select select;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		mouseManager.addMouseListener(this);
	}

	@Override
	public void onMouseEvent() {
		if (uiContext.getSelectedWidget() != null) {
			return;
		}

		if (inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 0
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& !dragContext.dragInProgress()) {

			PickingResult pickingResult = select.pick(Mouse.getX() - uiContext.getWindow().getWidth() / 2,
					Mouse.getY() - uiContext.getWindow().getHeight() / 2);
			if (pickingResult.getTargetType() != null) {
				return;
			}

			dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
			dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());
			dragContext.setDragType(DragType.WORLD);

			final Vector2f oldFP = dragContext.getOldFP();
			final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
			if (oldFP != null) {
				final ViewPort viewport = uiContext.getViewport();
				final Vector2f fp = viewport.getFocalPoint();
				fp.x = (oldFP.x * uiContext.getViewport().getZoomFactor() - (gameMouse.getX() - oldMousePosInWindow.x)) / uiContext.getViewport().getZoomFactor();
				fp.y = (oldFP.y * uiContext.getViewport().getZoomFactor() + (gameMouse.getY() - oldMousePosInWindow.y)) / uiContext.getViewport().getZoomFactor();
			}
			inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
		}
		while (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& dragContext.dragInProgress(DragType.WORLD)) {

			final Vector2f oldFP = dragContext.getOldFP();
			final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
			if (oldFP != null) {
				final ViewPort viewport = uiContext.getViewport();
				final Vector2f fp = viewport.getFocalPoint();
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
