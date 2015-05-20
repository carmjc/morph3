package net.carmgate.morph.ui.renderers;


public interface Renderer<T extends Renderable> {

   default void init() {
   }

   void render(T renderable);
}
