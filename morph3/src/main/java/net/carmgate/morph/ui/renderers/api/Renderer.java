package net.carmgate.morph.ui.renderers.api;


public interface Renderer<T extends Renderable> {

   void init();

   void render(T renderable);
}
