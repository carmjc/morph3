package net.carmgate.morph.ui.actions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.inputs.UIEvent;

@Singleton
public class DragContext {
	static public enum DragType {
		WORLD,
		WIDGET,
		COMPONENT;
	}

	@Inject private Logger LOGGER;

	private Vector2f oldFP;
	private Vector2f oldMousePosInWindow;
	private DragType dragType;

	public boolean dragInProgress() {
		return oldFP != null;
	}

	public boolean dragInProgress(DragType dragType) {
		return oldFP != null && dragType == this.dragType;
	}

	public DragType getDragType() {
		return dragType;
	}

	public Vector2f getOldFP() {
		return oldFP;
	}

	public Vector2f getOldMousePosInWindow() {
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

	public void setOldFP(Vector2f oldFP) {
		if (this.oldFP == null) {
			this.oldFP = new Vector2f(oldFP);
		} else {
			this.oldFP.copy(oldFP);
		}
	}

	public void setOldMousePosInWindow(float x, float y) {
		if (oldMousePosInWindow == null) {
			oldMousePosInWindow = new Vector2f(x, y);
		} else {
			oldMousePosInWindow.copy(x, y);
		}
	}
}
