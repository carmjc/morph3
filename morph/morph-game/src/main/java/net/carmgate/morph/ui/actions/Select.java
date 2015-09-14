package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.NeedsTarget;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.SelectRenderer.TargetType;
import net.carmgate.morph.ui.widgets.Widget;

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

	@Inject private Logger LOGGER;
	@Inject private MouseManager mouseManager;
	@Inject private InputHistory inputHistory;
	@Inject private UIContext uiContext;
	@Inject private World world;
	@Inject private GameMouse gameMouse;
	@Inject private DragContext dragContext;

	public Select() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		mouseManager.addMouseListener(this);
	}

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
				world.toggleTimeFrozen();
			} else if (cmp.canBeActivated()) {
				cmp.startBehavior();

			}
			LOGGER.debug("cmp");
		} else {
			// picked something not selectable
			uiContext.setSelectedWidget(null);
			uiContext.setSelectedShip(null);
			uiContext.setSelectedCmp(null);
		}

		return pickingResult.getTargetType();
	}

}
