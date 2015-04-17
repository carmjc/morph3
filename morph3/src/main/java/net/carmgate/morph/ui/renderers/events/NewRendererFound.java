package net.carmgate.morph.ui.renderers.events;

import net.carmgate.morph.ui.renderers.api.Renderable;
import net.carmgate.morph.ui.renderers.api.Renderer;

public class NewRendererFound {

	private final Renderer<? extends Renderable> renderer;

	public NewRendererFound(Renderer<? extends Renderable> renderer) {
		this.renderer = renderer;
	}

	public Renderer<? extends Renderable> getRenderer() {
		return renderer;
	}
}
