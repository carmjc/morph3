package net.carmgate.morph.ui.renderers.entities.ship;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.Collection;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Component;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

   @Inject private UIContext uiContext;
   @Inject private World world;

   private static TrueTypeFont font;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      if (font == null) {
         //      Font awtFont = new Font("Verdana", Font.PLAIN, 11);
         Font awtFont;
         try {
            awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("fonts/Rock_Elegance.otf"));
            awtFont = awtFont.deriveFont(20f); // set font size
            font = new TrueTypeFont(awtFont, true);
         } catch (FontFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
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
            width / 2f + 1 / zoom / massScale,
            1 / zoom / massScale,
            1 / zoom / massScale,
            new float[] { 0f, 0f, 0f, 1f },
            ship.getPlayer().getColor(),
            new float[] { 0f, 0f, 0f, 0f });
      RenderUtils.renderCircle(0,
            width / 2f - 4 / zoom / massScale,
            0,
            1 / zoom / massScale,
            new float[] { 1f, 1f, 1f, 0.2f },
            new float[] { 0.7f, 0.7f, 0.7f, 1f },
            new float[] { 0f, 0f, 0f, 1f });

      // render components and their states
      renderComponents(ship);

      GL11.glScalef(1f / massScale, 1f / massScale, 0);

      if (uiContext.getRenderMode() == RenderMode.DEBUG) {
         // Accel
         Vector2f accel = new Vector2f(ship.getAccel());
         RenderUtils.renderLine(Vector2f.NULL, accel, 2, 2, new float[] { 1f, 0f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
         // Speed
         Vector2f speed = new Vector2f(ship.getSpeed());
         RenderUtils.renderLine(Vector2f.NULL, speed, 2, 2, new float[] { 0f, 1f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });

         RenderUtils.renderLine(Vector2f.NULL, ship.debug1, 2, 2, new float[] { 0f, 0f, 1f, 1f }, new float[] { 0f, 0f, 0f, 0f });
         RenderUtils.renderLine(Vector2f.NULL, ship.debug2, 2, 2, new float[] { 0.5f, 0.5f, 0.5f, 1f }, new float[] { 0f, 0f, 0f, 0f });
         RenderUtils.renderLine(Vector2f.NULL, ship.debug3, 2, 2, new float[] { 1f, 1f, 1f, 1f }, new float[] { 0f, 0f, 0f, 0f });
         RenderUtils.renderLine(Vector2f.NULL, ship.debug4, 2, 2, new float[] { 1f, 1f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
      }
   }

   /**
    * @param ship
    * @param massScale
    * @param width
    */
   private void renderComponents(Ship ship) {
      float zoom = uiContext.getViewport().getZoomFactor();
      final float massScale = ship.getMass();
      final float width = 128;

      if (ship == uiContext.getSelectedShip()) {
         float colorScale = (int) (world.getTime() % 1000);
         colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
         RenderUtils.renderCircle(width / 2f + 12 / massScale,
               width / 2f + 14 / massScale,
               1 / zoom / massScale,
               5 / zoom / massScale,
               new float[] { 0f, 0f, 0f, 0f },
               new float[] { 1f, 1f, 1f, 0.5f * colorScale },
               new float[] { 0f, 0f, 0f, 0f });
      } else {
         RenderUtils.renderCircle(width / 2f + 12 / massScale,
               width / 2f + 14 / massScale,
               1 / zoom / massScale,
               1 / zoom / massScale,
               new float[] { 0f, 0f, 0f, 0f },
               new float[] { 1f, 1f, 1f, 0.1f },
               new float[] { 0f, 0f, 0f, 0f });
      }

      Collection<Component> components = ship.getComponents().values();
      int cmpNb = components.size();
      float i = 0;
      float rotSpeed = 0;// 1f / 100 * ship.getSpeed().length() / 20;
      GL11.glRotatef(rotSpeed * world.getTime(), 0, 0, 1);
      for (Component cmp : components) {
         GL11.glRotatef(i * 360 / cmpNb, 0, 0, 1);
         GL11.glTranslatef(0, -width / 2 - 13 / massScale, 0);
         GL11.glRotatef(-i * 360 / cmpNb - rotSpeed * world.getTime(), 0, 0, 1);
         GL11.glScalef(1f / massScale, 1f / massScale, 0);

         String str = cmp.getClass().getSimpleName();
         CharSequence ss = str.subSequence(0, 1);
         Color color = Color.white;
         if (!cmp.isActive()) {
            color = new Color(0.4f, 0.4f, 0.4f, 1f);
         }
         RenderUtils.renderText(font, -(float) font.getWidth(ss) / 2, (float) font.getHeight(ss) / 2, ss.toString(), 0, color);

         GL11.glScalef(massScale, massScale, 0);
         GL11.glRotatef(i * 360 / cmpNb + rotSpeed * world.getTime(), 0, 0, 1);
         GL11.glTranslatef(0, +width / 2 + 13 / massScale, 0);
         GL11.glRotatef(-i * 360 / cmpNb, 0, 0, 1);
         i++;
      }
      GL11.glRotatef(-rotSpeed * world.getTime(), 0, 0, 1);
   }

}
