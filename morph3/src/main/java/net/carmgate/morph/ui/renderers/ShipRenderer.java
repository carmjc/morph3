package net.carmgate.morph.ui.renderers;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

   @Inject
   private UIContext uiContext;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
   }

   @Override
   public void render(Ship ship) {

      // TODO
      final float mass = 2;
      final float massScale = mass / 10 * uiContext.getZoom();
      final float width = 128;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);
      RenderUtils.renderCircle(0,
            width / 2f - 2 / massScale,
            0,
            2 / massScale,
            new Float[] { 1f, 1f, 1f, 0.2f },
            new Float[] { 0.7f, 0.7f, 0.7f, 1f },
            new Float[] { 1f, 1f, 1f, 0f });
      RenderUtils.renderCircle(width / 2f - 2 / massScale,
            width / 2f + 2 / massScale,
            1 / massScale,
            1 / massScale,
            new Float[] { 1f, 1f, 1f, 0f },
            new Float[] { 1f, 0.5f, 0.5f, 1f },
            new Float[] { 0f, 0f, 0f, 0f });

      GL11.glScalef(1f / massScale, 1f / massScale, 0);
   }
}
