package net.carmgate.morph.ui.actions;

import javax.inject.Singleton;

import net.carmgate.morph.ui.inputs.common.UIEvent;

import org.lwjgl.util.vector.Vector2f;

@Singleton
public class DragContext {
	private Vector2f oldFP;
	private Vector2f oldMousePosInWindow;

	public boolean dragInProgress() {
		return oldFP != null;
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
	}

	public void setOldFP(Vector2f oldFP) {
		this.oldFP = oldFP;
	}

	public void setOldMousePosInWindow(Vector2f oldMousePosInWindow) {
		this.oldMousePosInWindow = oldMousePosInWindow;
	}
}
