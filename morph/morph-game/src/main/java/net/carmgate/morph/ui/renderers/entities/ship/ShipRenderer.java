package net.carmgate.morph.ui.renderers.entities.ship;

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
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.slf4j.Logger;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

   @Inject private UIContext uiContext;
   @Inject private World world;
   @Inject private Conf conf;
   @Inject private Logger LOGGER;

   private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
   private static Texture shipBgTexture;
   private static Texture shipTexture;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      try (BufferedInputStream shipBgInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture.bg"))); //$NON-NLS-1$
            BufferedInputStream shipInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture"))); //$NON-NLS-1$
            BufferedInputStream laserInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.laser.renderer.texture"))); //$NON-NLS-1$
            BufferedInputStream mlInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.miningLaser.renderer.texture"))); //$NON-NLS-1$
            BufferedInputStream repairerInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.repairer.renderer.texture"))); //$NON-NLS-1$
            BufferedInputStream propInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.propulsors.renderer.texture")))) { //$NON-NLS-1$
         shipBgTexture = TextureLoader.getTexture("PNG", shipBgInputStream);
         shipTexture = TextureLoader.getTexture("PNG", shipInputStream);
         cmpTextures.put(ComponentType.LASERS, TextureLoader.getTexture("PNG", laserInputStream));
         cmpTextures.put(ComponentType.MINING_LASERS, TextureLoader.getTexture("PNG", mlInputStream));
         cmpTextures.put(ComponentType.REPAIRER, TextureLoader.getTexture("PNG", repairerInputStream));
         cmpTextures.put(ComponentType.PROPULSORS, TextureLoader.getTexture("PNG", propInputStream));
      } catch (IOException e) {
         LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
      }
   }

   @Override
   public void render(Ship ship) {

      final float massScale = ship.getMass();
      final float width = 256;

      GL11.glScalef(massScale, massScale, 0);
      GL11.glColor4f(1f, 1f, 1f, 0.6f);
      GL11.glRotatef(ship.getRotate(), 0, 0, 1);

      renderComponents(ship);

      float[] color = ship.getPlayer().getColor();
      GL11.glColor4f(color[0] / 4, color[1] / 4, color[2] / 4, color[3]);
      RenderUtils.renderSprite(width, shipBgTexture);
      GL11.glColor4f(color[0], color[1], color[2], color[3]);
      RenderUtils.renderSprite(width, shipTexture);

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

      Collection<Component> components = ship.getComponents().values();
      for (Component cmp : components) {
         Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());
         Color color = new Color(Color.white);
         if (!cmp.isActive()) {
            color.a = 0.1f;
         }
         if (texture != null) {
            GL11.glColor4f(color.r, color.g, color.b, color.a);
            RenderUtils.renderSprite(width, texture);
         }
      }
   }

}
