package net.carmgate.morph.ui.widgets;

public abstract class Widget {

	private float[] position = new float[2];
	private int id;
	private boolean visible = true;

	public int getId() {
		return id;
	}

	public float[] getPosition() {
		return position;
	}

	public boolean isVisible() {
		return visible;
	}

	public abstract void renderWidget();

	public abstract void renderWidgetForSelect();

	public void setId(int id) {
		this.id = id;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
