package net.carmgate.morph.ui.actions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.dynamics.Body;
import org.slf4j.Logger;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.MWorld.TimeFreezeCause;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.DragContext;
import net.carmgate.morph.ui.inputs.DragContext.DragType;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.components.ComponentWidget;

@Singleton
public class WidgetDrag implements MouseListener {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private DragContext dragContext;
	@Inject private GameMouse gameMouse;
	@Inject private UIContext uiContext;
	@Inject private MWorld world;
	@Inject private ComponentManager cmpManager;

	@Override
	public void onMouseEvent() {
		if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 0
				&& !dragContext.dragInProgress()) {
			Body body = gameMouse.pick();

			if (body != null && body.getUserData() instanceof ComponentWidget) {
				dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());
				dragContext.setDragType(DragType.WIDGET);

				Widget widget = (Widget) body.getUserData();
				uiContext.setSelectedWidget(widget);
				if (!world.isTimeFrozen()) {
					world.toggleTimeFrozen(TimeFreezeCause.COMPONENT_DRAG);
				}

				LOGGER.debug("Widget dragged: " + widget + " - cmp: " + ((ComponentWidget) widget).getCmp());
				inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
			}
		}

		if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& dragContext.dragInProgress(DragType.WIDGET)) {

			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}

		if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& dragContext.dragInProgress(DragType.WIDGET)) {

			Body body = gameMouse.pick();

			if (body != null && !(body.getUserData() instanceof Widget)) {
				LOGGER.debug("Non widget body found");
				if (body.getUserData() instanceof PhysicalEntity && uiContext.getSelectedWidget() instanceof ComponentWidget) {
					ComponentWidget widget = (ComponentWidget) uiContext.getSelectedWidget();
					Component cmp = widget.getCmp();
					cmp.setTarget((PhysicalEntity) body.getUserData());
					if (cmpManager.canBeActivated(cmp)) {
						cmpManager.startBehavior(cmp);
					}
					LOGGER.debug("cmp (" + widget.getCmp() + ") target set: " + body.getUserData());
				}
			}

			if (world.getTimeFreezeCause() == TimeFreezeCause.COMPONENT_DRAG) {
				world.toggleTimeFrozen(TimeFreezeCause.COMPONENT_DRAG);
			}
			dragContext.reset();
			inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
		}
	}

}
