package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.World.TimeFreezeCause;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.actions.DragContext.DragType;
import net.carmgate.morph.ui.actions.Select.PickingResult;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.SelectRenderer.TargetType;

@Singleton
public class SetComponentTarget implements MouseListener {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;
	@Inject private MouseManager mouseManager;
	@Inject private Select select;
	@Inject private UIContext uiContext;
	@Inject private GameMouse gameMouse;
	@Inject private World world;
	@Inject private DragContext dragContext;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		mouseManager.addMouseListener(this);
	}

	@Override
	public void onMouseEvent() {
		if (inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_DOWN
				// && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& !dragContext.dragInProgress()) {
			if (gameMouse.pick() != null && gameMouse.pick().getTargetType() == TargetType.COMPONENT) {
				select.select();
			}
		}

		if (inputHistory.getLastMouseEvent(1).getButton() == 0 && inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
				&& inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				// && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
				&& !dragContext.dragInProgress()
				&& uiContext.getSelectedCmp() != null && uiContext.getSelectedCmp().isAvailable() && uiContext.getSelectedCmp().hasEnoughResources()) {
			dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
			dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());
			dragContext.setDragType(DragType.COMPONENT);
			if (!world.isTimeFrozen()) {
				world.toggleTimeFrozen(TimeFreezeCause.COMPONENT_DRAG);
			}
		}

		if (dragContext.dragInProgress(DragType.COMPONENT) && uiContext.getSelectedCmp() != null) {
			Component selectedCmp = uiContext.getSelectedCmp();
			PickingResult pickingResult = gameMouse.pick();
			if (pickingResult == null || pickingResult.getTarget() == null) {
				Vector2f targetPosInWorld = new Vector2f(gameMouse.getPosInWorld());
				selectedCmp.setTarget(null);
				selectedCmp.setTargetPosInWorld(targetPosInWorld);
			} else if (pickingResult.getTarget() instanceof Ship && ((Ship) pickingResult.getTarget()).getPlayer().getName().equals("Me")) {
				selectedCmp.setTarget(null);
			} else if (pickingResult.getTarget() instanceof PhysicalEntity) {
				selectedCmp.setTarget((PhysicalEntity) pickingResult.getTarget());
			}

			if (selectedCmp.getTargetPosInWorld() != null
					&& selectedCmp.getShip().getPos().distanceToSquared(selectedCmp.getTargetPosInWorld()) > selectedCmp.getRange()
					* selectedCmp.getRange()) {
				Vector2f newVect = selectedCmp.getTargetPosInWorld().clone().sub(selectedCmp.getShip().getPos());
				newVect.scale((selectedCmp.getRange() - 1) / newVect.length());
				selectedCmp.getTargetPosInWorld().copy(newVect).add(selectedCmp.getShip().getPos());
			}
		}

		if (inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
				&& uiContext.getSelectedCmp() != null) {
			Component selectedCmp = uiContext.getSelectedCmp();
			if (world.isTimeFrozen() && world.getTimeFreezeCause() == TimeFreezeCause.COMPONENT_DRAG) {
				world.toggleTimeFrozen(TimeFreezeCause.COMPONENT_DRAG);
			}
			if (selectedCmp.canBeActivated()) {
				selectedCmp.startBehavior();
			}
			dragContext.reset();
		}
	}

}
