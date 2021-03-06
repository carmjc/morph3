package net.carmgate.morph.ui.inputs;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.widgets.Widget;

/**
 * Allows mouse manipulation in world coordinates.
 */
@Singleton
public class GameMouse {

	@Inject private UIContext uiContext;
	@Inject private Logger LOGGER;
	@Inject private MWorld world;
	@Inject private Conf conf;

	private Vec2 posInWorld = new Vec2();
	private long lastPickingAbsoluteTime = 0;
	// private PickingResult pickingResult;

	public Vec2 getPosInWorld() {

		float zoomFactor = uiContext.getViewport().getZoomFactor();
		Vec2 focalPoint = uiContext.getViewport().getFocalPoint();

		int xInWorld = (int) ((getX() - uiContext.getWindow().getWidth() / 2 + focalPoint.x * zoomFactor) / zoomFactor);
		int yInWorld = (int) ((getY() - uiContext.getWindow().getHeight() / 2 - focalPoint.y * zoomFactor) / zoomFactor);

		return posInWorld.set(xInWorld, yInWorld);
	}

	/**
	 * @return mouse X position in window coordinates.
	 */
	public int getX() {
		return Mouse.getX();
	}

	/**
	 * @return mouse Y position in window coordinates.
	 */
	public int getY() {
		return Mouse.getY();
	}

	public boolean isButtonDown(int button) {
		return Mouse.isButtonDown(button);
	}

	public Body pick() {
		Vec2 posInBox2dWorld = getPosInWorld().mul(1f / 1000);
		Vec2 posInGui = new Vec2(getX() / 1000f, getY() / 1000f);

		Body body = world.getBox2dWorld().getBodyList();
		while (body != null) {
			if (body.getUserData() != null) {
				Fixture fixture = body.getFixtureList();
				while (fixture != null) {
					if (body.getUserData() instanceof Widget && fixture.testPoint(posInGui)) {
						return body;
					}
					if (!(body.getUserData() instanceof Widget) && fixture.testPoint(posInBox2dWorld)) {
						return body;
					}
					fixture = fixture.getNext();
				}
			}

			body = body.getNext();
		}

		return null;
	}

	// /**
	// * Picking under the mouse pointer.
	// *
	// * @return null if nothing picked
	// */
	// public PickingResult pick() {
	// return pick(Mouse.getX() - uiContext.getWindow().getWidth() / 2, Mouse.getY() - uiContext.getWindow().getHeight() / 2);
	// }

	// public PickingResult pick(int x, int y) {
	// // if picking has already been done very recently, don't do it again
	// if (world.getAbsoluteTime() - lastPickingAbsoluteTime < conf.getIntProperty("picking.interval")) {
	// return pickingResult;
	// }
	//
	// lastPickingAbsoluteTime = world.getAbsoluteTime();
	//
	// // LOGGER.debug("Picking at " + x + " " + y); //$NON-NLS-1$ //$NON-NLS-2$
	//
	// // get viewport
	// IntBuffer viewport = BufferUtils.createIntBuffer(16);
	// GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
	//
	// IntBuffer selectBuf = BufferUtils.createIntBuffer(512);
	// GL11.glSelectBuffer(selectBuf);
	// GL11.glRenderMode(GL11.GL_SELECT);
	//
	// GL11.glInitNames();
	//
	// GL11.glMatrixMode(GL11.GL_PROJECTION);
	// GL11.glPushMatrix();
	// GL11.glLoadIdentity();
	//
	// Window window = uiContext.getWindow();
	// GLU.gluPickMatrix(x, y, 1, 1, viewport);
	// GL11.glOrtho(0, window.getWidth(), 0, -window.getHeight(), 1, -1);
	// // GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	//
	// GL11.glMatrixMode(GL11.GL_MODELVIEW);
	// renderForSelect();
	//
	// GL11.glMatrixMode(GL11.GL_PROJECTION);
	// GL11.glPopMatrix();
	// GL11.glFlush();
	//
	// int hits = GL11.glRenderMode(GL11.GL_RENDER);
	// GL11.glMatrixMode(GL11.GL_MODELVIEW);
	// renderForSelect();
	//
	// // For debugging purpose only ...
	// // This allows to see the select buffer
	// // String result = "["; //$NON-NLS-1$
	// // for (int i = 0; i < selectBuf.capacity(); i++)
	// // {
	// // result += selectBuf.get(i) + ", "; //$NON-NLS-1$
	// // }
	// // LOGGER.debug("hits: " + hits + ", result : " + result + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	//
	// // Get the model elements picked
	// // The current index we are looking for in the select buffer
	//
	// if (hits == 0) {
	// return pickingResult = null;
	// }
	//
	// if (pickingResult == null) {
	// pickingResult = new PickingResult();
	// }
	//
	// // Iterate over the hits
	// // for (int i = 0; i < hits; i++) {
	// // get the number of names on this part of the stack
	// // int nbNames = selectBuf.get(selectBufIndex++);
	//
	// // jump over the two extremes of the picking z-index range
	// int selectBufIndex = 3;
	// int targetTypeId = selectBuf.get(selectBufIndex++);
	// Ship pickedShip = null;
	// PhysicalEntity pickedEntity = null;
	// if (targetTypeId == SelectRenderer.TargetType.WIDGET.ordinal()) {
	// Widget widget = uiContext.getWidgets().get(selectBuf.get(selectBufIndex++));
	//
	// if (widget instanceof ComponentWidget) {
	// pickingResult.setTargetType(TargetType.COMPONENT);
	// pickingResult.setTarget(((ComponentWidget) widget).getCmp());
	// } else {
	// pickingResult.setTargetType(TargetType.WIDGET);
	// pickingResult.setTarget(widget);
	// }
	// } else if (targetTypeId == SelectRenderer.TargetType.SHIP.ordinal()) {
	// // get the matching element in the model
	// int shipId = selectBuf.get(selectBufIndex++);
	// for (Ship ship : world.getShips()) { // FIXME We should implement the same logic as for widgets with a big map
	// if (ship.getId() == shipId) {
	// pickedShip = ship;
	// }
	// }
	// Ship playerShip = world.getPlayerShip();
	// if (uiContext.getRenderMode() == RenderMode.DEBUG
	// || pickedShip.getPos().distanceToSquared(playerShip.getPos()) <= playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius()) {
	// pickingResult.setTargetType(TargetType.SHIP);
	// pickingResult.setTarget(pickedShip);
	// }
	// } else if (targetTypeId == SelectRenderer.TargetType.NON_SHIP_PHYSICAL_ENTITY.ordinal()) {
	// // get the matching element in the model
	// int entityId = selectBuf.get(selectBufIndex++);
	// for (PhysicalEntity entity : world.getNonShipsPhysicalEntities()) { // FIXME We should implement the same logic as for widgets with a big map
	// if (entity.getId() == entityId) {
	// pickedEntity = entity;
	// }
	// }
	// Ship playerShip = world.getPlayerShip();
	// if (uiContext.getRenderMode() == RenderMode.DEBUG
	// || pickedEntity.getPos().distanceToSquared(playerShip.getPos()) <= playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius()) {
	// pickingResult.setTargetType(TargetType.NON_SHIP_PHYSICAL_ENTITY);
	// pickingResult.setTarget(pickedEntity);
	// }
	// // } else if (targetTypeId == SelectRenderer.TargetType.COMPONENT.ordinal()) {
	// // // get the matching element in the model
	// // int shipId = selectBuf.get(selectBufIndex++);
	// // for (Ship ship : world.getShips()) { // FIXME We should implement the same logic as for widgets with a big map
	// // if (ship.getId() == shipId) {
	// // pickedShip = ship;
	// // }
	// // }
	// // int cmpId = selectBuf.get(selectBufIndex++);
	// // Component pickedCmp = null;
	// // for (Component cmp : pickedShip.getComponents().values()) {
	// // if (cmp.getId() == cmpId) {
	// // pickedCmp = cmp;
	// // }
	// // }
	// // pickingResult.setTargetType(TargetType.COMPONENT);
	// // pickingResult.setTarget(pickedCmp);
	// }
	// // }
	// // LOGGER.debug("Select " + pickingResult.getTargetType());
	// return pickingResult;
	// }

	/**
	 * Renders the scene for selection.
	 * Can also be used directly for debugging purposes to show the pickable areas.
	 *
	 * @param zoomFactor
	 * @param glMode
	 */
	// public void renderForSelect() {
	//
	// Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
	// float zoomFactor = uiContext.getViewport().getZoomFactor();
	//
	// float x = -uiContext.getWindow().getWidth() / 2;
	// float y = -uiContext.getWindow().getHeight() / 2;
	// GL11.glTranslatef(x, y, 0);
	// renderWidgetForSelect(uiContext.getWidgetRoot());
	// GL11.glTranslatef(-x, -y, 0);
	//
	// if (uiContext.getContext() == Context.GAME) {
	// GL11.glScalef(zoomFactor, zoomFactor, 1);
	// GL11.glTranslatef(-focalPoint.x, -focalPoint.y, -0);
	//
	// for (Ship ship : world.getShips()) {
	// final Vector2f pos = ship.getPos();
	// GL11.glTranslatef(pos.x, pos.y, 0);
	// shipSelectRenderer.render(ship, 1f);
	// GL11.glTranslatef(-pos.x, -pos.y, 0);
	// }
	//
	// for (PhysicalEntity entity : world.getNonShipsPhysicalEntities()) {
	// final Vector2f pos = entity.getPos();
	// GL11.glTranslatef(pos.x, pos.y, 0);
	// physicalEntitySelectRenderer.render(entity, 1f);
	// GL11.glTranslatef(-pos.x, -pos.y, 0);
	// }
	//
	// GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
	// GL11.glScalef(1f / zoomFactor, 1f / zoomFactor, 1);
	// }
	// }

	// private void renderWidgetForSelect(Widget widget) {
	// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
	// GL11.glPushName(widget.getId());
	// widget.renderInteractiveAreas();
	// GL11.glPopName();
	// GL11.glPopName();
	// }

	@Override
	public String toString() {
		return "GameMouse[" + getX() + "x" + getY() + "]";
	}
}
