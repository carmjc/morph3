package net.carmgate.morph.ui.renderers;


public interface Renderer<T extends Renderable> {

   void init();

   void render(T renderable);
}
