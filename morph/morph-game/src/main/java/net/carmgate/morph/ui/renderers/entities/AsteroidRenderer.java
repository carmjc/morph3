package net.carmgate.morph.ui.renderers.entities;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.slf4j.Logger;

public class AsteroidRenderer implements Renderer<Asteroid> {

   @Inject private Logger LOGGER;

   private static Texture asteroids1Texture;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      // load texture from PNG file if needed
      if (asteroids1Texture == null) {
         try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream("img/asteroids1.png"))) {
            asteroids1Texture = TextureLoader.getTexture("PNG", fileInputStream);
         } catch (IOException e) {
            LOGGER.error("Exception raised while loading texture", e);
         }
      }
   }

   @Override
   public void render(Asteroid asteroid) {
      float massScale = asteroid.getMass();
      float width = 128f;

      int i = 2; // TODO variabilize this
      int j = 4;

      GL11.glColor4f(1, 1, 1, 1);
      GL11.glScalef(massScale, massScale, 0);
      GL11.glRotatef(asteroid.getRotate(), 0, 0, 1);
      asteroids1Texture.bind();
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(i / 8f, j / 8f);
      GL11.glVertex2f(-width / 2, -width / 2);
      GL11.glTexCoord2f((i + 1) / 8f, j / 8f);
      GL11.glVertex2f(width / 2, -width / 2);
      GL11.glTexCoord2f((i + 1) / 8f, (j + 1) / 8f);
      GL11.glVertex2f(width / 2, width / 2);
      GL11.glTexCoord2f(i / 8f, (j + 1) / 8f);
      GL11.glVertex2f(-width / 2, width / 2);
      GL11.glEnd();
      GL11.glRotatef(-asteroid.getRotate(), 0, 0, 1);
      GL11.glScalef(1 / massScale, 1 / massScale, 0);

   }

}
