package net.carmgate.morph.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.renderers.RenderMode;

@Singleton
public class UIContext {

   @Inject private ViewPort viewport;
   @Inject private Window window;

   private RenderMode renderMode = RenderMode.NORMAL;
   private Ship selectedShip;

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

   public void setSelectedShip(Ship selectedShip) {
      this.selectedShip = selectedShip;
   }

   public Ship getSelectedShip() {
      return selectedShip;
   }
}
