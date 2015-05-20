package net.carmgate.morph.ui.renderers.entities.ship;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;

@Singleton
public class ShipSelectRenderer implements SelectRenderer<Ship> {

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void render(Ship ship) {
      final float massScale = ship.getMass();
      final float width = 128;

      GL11.glScalef(massScale, massScale, 0);
      RenderUtils.renderCircle(0,
            width / 2f,
            0,
            0,
            new float[] { 1f, 1f, 1f, 1f },
            new float[] { 1f, 1f, 1f, 1f },
            new float[] { 1f, 1f, 1f, 1f });
      GL11.glScalef(1f / massScale, 1f / massScale, 0);
   }

}
