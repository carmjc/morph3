package net.carmgate.morph.ui;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetContainer;

@Singleton
public class UIContext {

   @Inject private ViewPort viewport;
   @Inject private Window window;
   @Inject private MEventManager eventManager;

   private RenderMode renderMode = RenderMode.NORMAL;
   private Ship selectedShip;
   private Widget selectedWidget;
   private WidgetContainer widgetRoot;
   private Map<Integer, Widget> widgets = new WeakHashMap<>();

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

   public Widget getSelectedWidget() {
      return selectedWidget;
   }

   public void setSelectedWidget(Widget selectedWidget) {
      this.selectedWidget = selectedWidget;
   }

   public WidgetContainer getWidgetRoot() {
      return widgetRoot;
   }

   public Map<Integer, Widget> getWidgets() {
      return widgets;
   }

   public void setWidgetRoot(WidgetContainer widgetRoot) {
      this.widgetRoot = widgetRoot;
   }

   public void setWidgets(Map<Integer, Widget> widgets) {
      this.widgets = widgets;
   }
}
