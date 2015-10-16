package net.carmgate.morph.ui.renderers;

import net.carmgate.morph.model.geometry.Vec2;

public class StringRenderable implements Renderable {

	private String str;
	private Vec2 pos = new Vec2();

	private float size;
	public StringRenderable() {
	}

	public Vec2 getPos() {
		return pos;
	}

	public float getSize() {
		return size;
	}

	public String getStr() {
		return str;
	}

	public void setFontSize(float size) {
		this.size = size;
	}

	public void setStr(String str) {
		this.str = str;
	}
}
