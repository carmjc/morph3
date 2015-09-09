package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.actions.Select.PickingResult;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

@Singleton
public class SetComponentTarget implements MouseListener {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private MouseManager mouseManager;
	@Inject private Select select;
	@Inject private UIContext uiContext;
	@Inject private GameMouse gameMouse;
	@Inject private World world;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		mouseManager.addMouseListener(this);
	}

	@Override
	public void onMouseEvent() {
		Component selectedCmp = uiContext.getSelectedCmp();
		if (inputHistory.getLastMouseEvent(1).getButton() == 1 && inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent(1).getButton() == 1
				&& inputHistory.getLastMouseEvent().getButton() == 1 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& selectedCmp != null && !selectedCmp.isActive()) {

			Ship selectedShip = selectedCmp.getShip();
			PickingResult pickingResult = select.pick(Mouse.getX() - uiContext.getWindow().getWidth() / 2,
					Mouse.getY() - uiContext.getWindow().getHeight() / 2);

			if (pickingResult.getTarget() == null) {
				selectedCmp.setTargetPosInWorld(gameMouse.getPosInWorld());
				LOGGER.debug("Target set to position: " + gameMouse.getPosInWorld() + " for "
						+ selectedCmp.getClass().getAnnotation(ComponentKind.class).value());
				if (world.isTimeFrozen()) {
					world.toggleTimeFrozen();
				}
				if (selectedCmp.canBeActivated()) {
					selectedCmp.startBehavior();
				}
			} else if (pickingResult.getTarget() instanceof PhysicalEntity) {
				selectedCmp.setTarget((PhysicalEntity) pickingResult.getTarget());
				LOGGER.debug("Target set to: " + pickingResult.getTarget());
				if (world.isTimeFrozen()) {
					world.toggleTimeFrozen();
				}
				if (selectedCmp.canBeActivated()) {
					selectedCmp.startBehavior();
				}
			}
		}
	}

}
