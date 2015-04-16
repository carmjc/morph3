package net.carmgate.morph.model.renderers;

public interface Renderer<T extends Renderable> {

   void init();

   void render(T renderable);
}
