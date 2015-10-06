package net.carmgate.morph.ui.renderers;

import java.nio.FloatBuffer;

public interface Renderer<T extends Renderable> {

	void clean();

	default void init() {}

	void prepare();

	void render(T renderable, float alpha, FloatBuffer vpBuffer);
}
