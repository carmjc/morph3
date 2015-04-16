package net.carmgate.morph.model.renderers.events;

import net.carmgate.morph.model.renderers.Renderable;
import net.carmgate.morph.model.renderers.Renderer;

public class NewRendererFound {

	private final Renderer<? extends Renderable> renderer;

	public NewRendererFound(Renderer<? extends Renderable> renderer) {
		this.renderer = renderer;
	}

	public Renderer<? extends Renderable> getRenderer() {
		return renderer;
	}
}
