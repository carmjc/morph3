package net.carmgate.morph.ui.renderers.entities.ship;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.text.MessageFormat;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Component;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
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
            awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("fonts/jagw____.ttf"));
            awtFont = awtFont.deriveFont(12f); // set font size
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
      final float selectedWidth = 128 + 12 / zoom / massScale;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);

      if (ship == uiContext.getSelectedShip()) {
         float colorScale = (int) (world.getTime() % 1000);
         colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.5f;
         RenderUtils.renderCircle(selectedWidth / 2f - 1 / zoom / massScale,
               selectedWidth / 2f + 1 / zoom / massScale,
               1 / zoom / massScale,
               6 / zoom / massScale,
               new float[] { 0f, 0f, 0f, 0f },
               new float[] { 1f, 1f, 1f, 0.5f * colorScale },
               new float[] { 0f, 0f, 0f, 0f });
      }

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
      GL11.glScalef(1f / massScale, 1f / massScale, 0);

      if (ship == uiContext.getSelectedShip()) {
         // renderText(ship);
      }

      if (ship == uiContext.getSelectedShip()) {
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

   public void renderText(Ship ship) {
      float zoom = uiContext.getViewport().getZoomFactor();
      final float massScale = ship.getMass();

      // render textual information
      GL11.glScalef(1 / zoom, 1 / zoom, 1);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Distance: {0,number,#.###}", ship.debug1.length()), -4, Color.white);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Speed: {0,number,#.###}", ship.getSpeed().length()), -3, Color.white);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Accel: {0,number,#.###}", ship.getAccel().length()), -2, Color.white);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Health: {0,number,#.###}", ship.getHealth()), -1, Color.white);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Energy: {0,number,#.###}", ship.getEnergy()), 0, Color.white);
      RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0, MessageFormat.format("Resources: {0,number,#.###}", ship.getResources()), 1, Color.white);
      int i = 2;
      for (Component c : ship.getComponents().values()) {
         Color color = Color.white;
         if (!c.isActive()) {
            color = Color.red;
         }
         RenderUtils.renderText(font, Math.round(80 * massScale * zoom), 0,
               MessageFormat.format(c.getClass().getSimpleName() + " - de/dt: {0,number,#.###}, dr/dt: {1,number,#.###}", c.getEnergyDt(), c.getResourcesDt()), i++, color);
      }
      GL11.glScalef(zoom, zoom, 1);

   }
}
