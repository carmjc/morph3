package net.carmgate.morph.ui.renderers.entities.ship;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

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
      float zoom = uiContext.getViewport().getZoomFactor();
      final float massScale = ship.getMass();
      final float width = 128;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);
      RenderUtils.renderCircle(width / 2f - 2 / zoom / massScale,
            width / 2f + 2 / zoom / massScale,
            1 / zoom / massScale,
            1 / zoom / massScale,
            new float[] { 0f, 0f, 0f, 1f },
            ship.getPlayer().getColor(),
            new float[] { 0f, 0f, 0f, 0f });
      RenderUtils.renderCircle(0,
            width / 2f - 2 / zoom / massScale,
            0,
            1 / zoom / massScale,
            new float[] { 1f, 1f, 1f, 0.2f },
            new float[] { 0.7f, 0.7f, 0.7f, 1f },
            new float[] { 0f, 0f, 0f, 1f });

      GL11.glScalef(1f / massScale, 1f / massScale, 0);

      // Render kinematics
      // Accel
      Vector2f accel = new Vector2f(ship.getAccel());
      RenderUtils.renderLine(Vector2f.NULL, accel, 2, 2, new float[] { 1f, 0f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
      // Accel
      Vector2f speed = new Vector2f(ship.getSpeed());
      RenderUtils.renderLine(Vector2f.NULL, speed, 2, 2, new float[] { 0f, 1f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
   }
}
