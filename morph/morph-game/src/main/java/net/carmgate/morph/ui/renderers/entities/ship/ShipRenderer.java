package net.carmgate.morph.ui.renderers.entities.ship;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
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
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

   @Inject private UIContext uiContext;
   @Inject private World world;
   @Inject private Conf conf;
   @Inject private Logger LOGGER;

   private static TrueTypeFont font;
   private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
   private static Texture shipBgTexture;
   private static Texture shipTexture;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      //      Font awtFont = new Font("Verdana", Font.PLAIN, 11);
      Font awtFont;
      try {
         awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(conf.getProperty("ship.renderer.font"))); //$NON-NLS-1$
         awtFont = awtFont.deriveFont(conf.getFloatProperty("ship.renderer.font.size")); // set font size //$NON-NLS-1$
         font = new TrueTypeFont(awtFont, true);
      } catch (FontFormatException | IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture.bg")))) { //$NON-NLS-1$
         shipBgTexture = TextureLoader.getTexture("PNG", fileInputStream);
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture")))) { //$NON-NLS-1$
         shipTexture = TextureLoader.getTexture("PNG", fileInputStream);
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.laser.renderer.texture")))) { //$NON-NLS-1$
         cmpTextures.put(ComponentType.LASERS, TextureLoader.getTexture("PNG", fileInputStream));
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.miningLaser.renderer.texture")))) { //$NON-NLS-1$
         cmpTextures.put(ComponentType.MINING_LASERS, TextureLoader.getTexture("PNG", fileInputStream));
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.repairer.renderer.texture")))) { //$NON-NLS-1$
         cmpTextures.put(ComponentType.REPAIRER, TextureLoader.getTexture("PNG", fileInputStream));
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }

      try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.propulsors.renderer.texture")))) { //$NON-NLS-1$
         cmpTextures.put(ComponentType.PROPULSORS, TextureLoader.getTexture("PNG", fileInputStream));
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }
   }

   @Override
   public void render(Ship ship) {

      // TODO
      float zoom = uiContext.getViewport().getZoomFactor();
      final float massScale = ship.getMass();
      final float width = 256;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);
      GL11.glRotatef(ship.getRotate(), 0, 0, 1);

      // RenderUtils.renderCircle(width / 2f - 2 / zoom / massScale,
      // width / 2f + 1 / zoom / massScale,
      // 1 / zoom / massScale,
      // 1 / zoom / massScale,
      // new float[] { 0f, 0f, 0f, 1f },
      // ship.getPlayer().getColor(),
      // new float[] { 0f, 0f, 0f, 0f });
      // RenderUtils.renderCircle(0,
      // width / 2f - 4 / zoom / massScale,
      // 0,
      // 1 / zoom / massScale,
      // new float[] { 1f, 1f, 1f, 0.2f },
      // new float[] { 0.7f, 0.7f, 0.7f, 1f },
      // new float[] { 0f, 0f, 0f, 1f });

      // render components and their states
      renderComponents(ship);

      float[] color = ship.getPlayer().getColor();
      GL11.glColor4f(color[0] / 4, color[1] / 4, color[2] / 4, color[3]);
      shipBgTexture.bind();
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0f, 0f);
      GL11.glVertex2f(-width / 2, -width / 2);
      GL11.glTexCoord2f(1f, 0f);
      GL11.glVertex2f(width / 2, -width / 2);
      GL11.glTexCoord2f(1f, 1f);
      GL11.glVertex2f(width / 2, width / 2);
      GL11.glTexCoord2f(0f, 1f);
      GL11.glVertex2f(-width / 2, width / 2);
      GL11.glEnd();
      GL11.glColor4f(color[0], color[1], color[2], color[3]);
      shipTexture.bind();
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0f, 0f);
      GL11.glVertex2f(-width / 2, -width / 2);
      GL11.glTexCoord2f(1f, 0f);
      GL11.glVertex2f(width / 2, -width / 2);
      GL11.glTexCoord2f(1f, 1f);
      GL11.glVertex2f(width / 2, width / 2);
      GL11.glTexCoord2f(0f, 1f);
      GL11.glVertex2f(-width / 2, width / 2);
      GL11.glEnd();

      GL11.glRotatef(-ship.getRotate(), 0, 0, 1);
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
      final float width = 256;

      if (ship == uiContext.getSelectedShip()) {
         float colorScale = (int) (world.getTime() % 1000);
         colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
         RenderUtils.renderCircle(width / 2f + 10 / massScale,
               width / 2f + 10 / massScale,
               2 / zoom / massScale,
               5 / zoom / massScale,
               new float[] { 0f, 0f, 0f, 0f },
               new float[] { 1f, 1f, 1f, 0.5f * colorScale },
               new float[] { 0f, 0f, 0f, 0f });
      }
      // else {
      // RenderUtils.renderCircle(width / 2f + 18 / massScale,
      // width / 2f + 18 / massScale,
      // 2 / zoom / massScale,
      // 2 / zoom / massScale,
      // new float[] { 0f, 0f, 0f, 0f },
      // new float[] { 1f, 1f, 1f, 0.1f },
      // new float[] { 0f, 0f, 0f, 0f });
      // }

      Collection<Component> components = ship.getComponents().values();
      // int cmpNb = components.size();
      // float i = 0;
      // float rotSpeed = 0;// 1f / 100 * ship.getSpeed().length() / 20;
      // GL11.glRotatef(rotSpeed * world.getTime(), 0, 0, 1);
      for (Component cmp : components) {
         // GL11.glRotatef(i * 360 / cmpNb, 0, 0, 1);
         // GL11.glTranslatef(0, -width / 2 - 20 / massScale, 0);
         // GL11.glRotatef(-i * 360 / cmpNb - rotSpeed * world.getTime(), 0, 0, 1);
         // GL11.glScalef(1f / massScale, 1f / massScale, 0);

         // RenderUtils.renderCircle(0f, 12f, 0, 2f / zoom, new float[] { 0f, 0f, 0f, 0f }, new float[] { 0.5f, 0.5f, 0.5f, 1f }, new float[] { 0f, 0f, 0f, 0f });
         Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());
         Color color = new Color(Color.white);
         if (!cmp.isActive()) {
            color.a = 0.1f;
         }
         if (texture != null) {
            GL11.glColor4f(color.r, color.g, color.b, color.a);
            // GL11.glScalef(1f / 5, 1f / 5, 0);
            texture.bind();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0f, 0f);
            GL11.glVertex2f(-width / 2, -width / 2);
            GL11.glTexCoord2f(1f, 0f);
            GL11.glVertex2f(width / 2, -width / 2);
            GL11.glTexCoord2f(1f, 1f);
            GL11.glVertex2f(width / 2, width / 2);
            GL11.glTexCoord2f(0f, 1f);
            GL11.glVertex2f(-width / 2, width / 2);
            GL11.glEnd();
            // GL11.glScalef(5f, 5f, 0);
         }
         // else {
         // String str = cmp.getClass().getSimpleName();
         // CharSequence ss = str.subSequence(0, 1);
         // RenderUtils.renderText(font, -(float) font.getWidth(ss) / 2, (float) font.getHeight(ss) / 2, ss.toString(), 0, color);
         // }

         // GL11.glScalef(massScale, massScale, 0);
         // GL11.glRotatef(i * 360 / cmpNb + rotSpeed * world.getTime(), 0, 0, 1);
         // GL11.glTranslatef(0, +width / 2 + 20 / massScale, 0);
         // GL11.glRotatef(-i * 360 / cmpNb, 0, 0, 1);
         // i++;
      }
      // GL11.glRotatef(-rotSpeed * world.getTime(), 0, 0, 1);
   }

}
