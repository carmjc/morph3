package net.carmgate.morph.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.ui.renderers.RenderMode;

@Singleton
public class UIContext {

   private RenderMode renderMode = RenderMode.NORMAL;
   @Inject
   private ViewPort viewport;
   @Inject
   private Window window;

   public RenderMode getRenderMode() {
      return renderMode;
   }

   public ViewPort getViewport() {
      return viewport;
   }

   public Window getWindow() {
      return window;
   }

   public void setRenderMode(RenderMode renderMode) {
      this.renderMode = renderMode;
   }
}
