package net.carmgate.morph.ui.actions.selection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.UIContext.Context;
import net.carmgate.morph.ui.inputs.DragContext;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.SelectRenderer.TargetType;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;

@Singleton
public class Select implements MouseListener {

	public static class PickingResult {
		private TargetType targetType = null;
		private Object target;

		public Object getTarget() {
			return target;
		}

		public TargetType getTargetType() {
			return targetType;
		}

		public void setTarget(Object target) {
			this.target = target;
		}

		public void setTargetType(TargetType targetType) {
			this.targetType = targetType;
		}
	}

	@Inject private ComponentManager componentManager;

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;
	@Inject private World world;
	@Inject private GameMouse gameMouse;
	@Inject private DragContext dragContext;

	@Override
	public void onMouseEvent() {
		if (inputHistory.getLastMouseEvent(1).getButton() == 0 && inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 0
				&& inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& !dragContext.dragInProgress()) {
			select();
		}
	}

	/**
	 * Selects model elements.
	 */
	public TargetType select() {

		PickingResult pickingResult = gameMouse.pick();

		if (uiContext.getContext() == Context.GAME) {
			if (pickingResult == null) {
				uiContext.setSelectedWidget(null);
				uiContext.setSelectedShip(null);
				uiContext.setSelectedCmp(null);
				return null;
			}

			if (pickingResult.getTargetType() == TargetType.WIDGET) {
				uiContext.setSelectedWidget((Widget) pickingResult.getTarget());
				// LOGGER.debug("widget");
			} else if (pickingResult.getTargetType() == TargetType.SHIP) {
				uiContext.setSelectedShip((Ship) pickingResult.getTarget());
				uiContext.setSelectedCmp(null);
				// LOGGER.debug("ship");
			} else if (pickingResult.getTargetType() == TargetType.COMPONENT) {
				Component cmp = (Component) pickingResult.getTarget();
				uiContext.setSelectedShip(cmp.getShip());
				uiContext.setSelectedCmp(cmp);
				if (cmp.getTarget() == null && !world.isTimeFrozen()
						&& cmp.getClass().isAnnotationPresent(NeedsTarget.class)) {
					// world.toggleTimeFrozen(TimeFreezeCause.COMPONENT_DRAG);
				} else if (componentManager.canBeActivated(cmp)) {
					componentManager.startBehavior(cmp);

				}
			} else {
				// picked something not selectable
				uiContext.setSelectedWidget(null);
				uiContext.setSelectedShip(null);
				uiContext.setSelectedCmp(null);
			}
		}

		if (uiContext.getContext() == Context.SHIP_EDITOR) {

			if (pickingResult == null) {
				uiContext.setSelectedWidget(null);
				return null;
			}

			if (pickingResult.getTargetType() == TargetType.WIDGET) {
				Widget widget = (Widget) pickingResult.getTarget();
				if (widget instanceof WidgetMouseListener) {
					uiContext.setSelectedWidget(widget);
					((WidgetMouseListener) widget).onClick();
				}
			}
		}

		// Safe guard
		if (pickingResult == null) {
			return null;
		}

		return pickingResult.getTargetType();
	}

}
