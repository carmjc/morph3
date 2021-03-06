package net.carmgate.morph.ui.widgets;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.widgets.containers.WidgetContainer;

public abstract class Widget implements WidgetMouseListener {

	private float[] position;
	private int id;
	private boolean visible = true;

	private float height;
	private float width;
	private float[] insets = new float[] { 0, 0, 0, 0 };
	private float[] outsets = new float[] { 0, 0, 0, 0 };
	private float[] bgColor = new float[] { 0, 0, 0, 0 };
	private final Map<LayoutHint, Float> layoutHints = new HashMap<>();

	private WidgetContainer parent;
	private List<WidgetMouseListener> widgetMouseListeners = new ArrayList<>();
	protected Shape shape;
	private Body body;

	public boolean addWidgetMouseListener(WidgetMouseListener e) {
		return widgetMouseListeners.add(e);
	}

	public float[] getBgColor() {
		return bgColor;
	}

	public float getHeight() {
		if (getParent() != null && getLayoutHints().containsKey(LayoutHint.FILL_VERTICAL)) {
			return getParent().getInnerHeight();
		}
		return height;
	}

	public int getId() {
		return id;
	}

	public float getInnerHeight() {
		return getHeight() - (getInsets()[0] + getInsets()[2] + getOutsets()[0] + getOutsets()[2]);
	}

	public float getInnerWidth() {
		return getWidth() - (getInsets()[1] + getInsets()[3] + getOutsets()[1] + getOutsets()[3]);
	}

	public float[] getInsets() {
		return insets;
	}

	public Map<LayoutHint, Float> getLayoutHints() {
		return layoutHints;
	}

	public float[] getOutsets() {
		return outsets;
	}

	public WidgetContainer getParent() {
		return parent;
	}

	public float[] getPosition() {
		float[] pos = position;
		if (pos == null) {
			pos = getParent().getPosition(this);
		}
		return pos;
	}

	public final Shape getShape() {
		return shape;
	}

	public float getWidth() {
		if (getParent() != null && getLayoutHints().containsKey(LayoutHint.FILL_HORIZONTAL)) {
			return getParent().getInnerWidth();
		}
		return width;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void onClick() {
		for (WidgetMouseListener listener : widgetMouseListeners) {
			listener.onClick();
		}
	}

	public boolean removeWidgetMouseListener(Object o) {
		return widgetMouseListeners.remove(o);
	}

	public abstract void renderWidget(Matrix4f m, FloatBuffer vpFb);

	public void setBgColor(float[] bgColor) {
		this.bgColor = bgColor;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInsets(float[] insets) {
		this.insets = insets;
	}

	public void setOutsets(float[] outsets) {
		this.outsets = outsets;
	}

	public void setParent(WidgetContainer parent) {
		this.parent = parent;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setWidth(float width) {
		this.width = width;
	}
}
