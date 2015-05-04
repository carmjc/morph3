package net.carmgate.morph.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.renderers.RenderMode;

@Singleton
public class UIContext {

   @Inject private ViewPort viewport;
   @Inject private Window window;
   @Inject private MEventManager eventManager;

   private RenderMode renderMode = RenderMode.NORMAL;
   private Ship selectedShip;

   @PostConstruct
   private void init() {
      eventManager.scanAndRegister(this);
   }

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

   public void onShipDeath(@MObserves ShipDeath shipDeath) {
      if (selectedShip == shipDeath.getShip()) {
         selectedShip = null;
      }
   }
}
