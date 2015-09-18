package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.actions.DragContext.DragType;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;

@Singleton
public class DragInWidget implements MouseListener {

	@Inject private InputHistory inputHistory;
	@Inject private DragContext dragContext;
	@Inject private UIContext uiContext;
	@Inject private GameMouse gameMouse;
	@Inject private Logger LOGGER;
	@Inject private MouseManager mouseManager;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		mouseManager.addMouseListener(this);
	}

	@Override
	public void onMouseEvent() {
		Widget widget = uiContext.getSelectedWidget();
		if (widget == null) {
			return;
		}

		if (inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 0
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& !dragContext.dragInProgress()) {
			dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
			dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());
			dragContext.setDragType(DragType.WIDGET);

			((WidgetMouseListener) widget).onDrag();

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
		}
		while (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& dragContext.dragInProgress(DragType.WIDGET)) {
			((WidgetMouseListener) widget).onDrag();

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
		if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& dragContext.dragInProgress(DragType.WIDGET)) {
			dragContext.reset();

			((WidgetMouseListener) widget).onDrag();

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
	}

}
