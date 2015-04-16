package net.carmgate.morph.model.entities;

import net.carmgate.morph.model.renderers.Renderable;

import org.lwjgl.util.vector.Vector2f;

public class Ship implements Renderable {

	private Vector2f pos;

	public Ship(Vector2f pos) {
		this.pos = pos;
	}

	public Vector2f getPos() {
		return pos;
	}

	public void setPos(Vector2f pos) {
		this.pos = pos;
	}

}
