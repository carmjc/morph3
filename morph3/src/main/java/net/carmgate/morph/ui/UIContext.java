package net.carmgate.morph.ui;

import javax.inject.Singleton;

import net.carmgate.morph.model.renderers.RenderMode;

@Singleton
public class UIContext {

   private RenderMode renderMode = RenderMode.NORMAL;
   private float zoom = 1f;

   public RenderMode getRenderMode() {
      return renderMode;
   }

   public void setRenderMode(RenderMode renderMode) {
      this.renderMode = renderMode;
   }

   public float getZoom() {
      return zoom;
   }

   public void setZoom(float zoom) {
      this.zoom = zoom;
   }
}
