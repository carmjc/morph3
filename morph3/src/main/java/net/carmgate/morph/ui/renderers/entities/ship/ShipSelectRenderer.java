package net.carmgate.morph.ui.renderers.entities.ship;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;

@Singleton
public class ShipSelectRenderer implements SelectRenderer<Ship> {

   @Inject
   private UIContext uiContext;

   @Override
   public void init() {
      // TODO Auto-generated method stub

   }

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void render(Ship ship) {
      final float mass = 2;
      final float massScale = mass / 10 * uiContext.getViewport().getZoomFactor();
      final float width = 128;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);
      RenderUtils.renderDisc(width / 2f);
   }

}
