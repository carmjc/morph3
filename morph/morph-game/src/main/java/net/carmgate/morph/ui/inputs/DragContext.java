package net.carmgate.morph.ui.inputs;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.common.Vec2;
import org.slf4j.Logger;

@Singleton
public class DragContext {
	static public enum DragType {
		WORLD,
		WIDGET,
		COMPONENT;
	}

	@Inject private Logger LOGGER;

	private Vec2 oldFP;
	private Vec2 oldMousePosInWindow;
	private DragType dragType;

	public boolean dragInProgress() {
		return oldFP != null || dragType != null;
	}

	public boolean dragInProgress(DragType dragType) {
		return (oldFP != null || dragType != null) && dragType == this.dragType;
	}

	public DragType getDragType() {
		return dragType;
	}

	public Vec2 getOldFP() {
		return oldFP;
	}

	public Vec2 getOldMousePosInWindow() {
		return oldMousePosInWindow;
	}

	public boolean hasBeenDragged(UIEvent event) {
		return oldMousePosInWindow != null
				&& (event.getPositionInWindow()[0] != oldMousePosInWindow.x || event.getPositionInWindow()[1] != oldMousePosInWindow.y);
	}

	public void reset() {
		oldFP = null;
		oldMousePosInWindow = null;
		dragType = null;
	}

	public void setDragType(DragType dragType) {
		this.dragType = dragType;
	}

	public void setOldFP(Vec2 oldFP) {
		if (this.oldFP == null) {
			this.oldFP = new Vec2(oldFP);
		} else {
			this.oldFP.set(oldFP);
		}
	}

	public void setOldMousePosInWindow(float x, float y) {
		if (oldMousePosInWindow == null) {
			oldMousePosInWindow = new Vec2(x, y);
		} else {
			oldMousePosInWindow.set(x, y);
		}
	}
}
