package net.carmgate.morph.ui.renderers;

import net.carmgate.morph.model.geometry.Vector2f;

public class StringRenderable implements Renderable {

	private String str;
	private Vector2f pos = new Vector2f();

	private float size;
	public StringRenderable() {
	}

	public Vector2f getPos() {
		return pos;
	}

	public float getSize() {
		return size;
	}

	public String getStr() {
		return str;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void setStr(String str) {
		this.str = str;
	}
}
