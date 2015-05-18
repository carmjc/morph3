package net.carmgate.morph.ui.widgets;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

public class ComponentPonderationWidget extends Widget {

   @Inject private UIContext uiContext;
   @Inject private Logger LOGGER;
   @Inject private Conf conf;

   private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
   private static TrueTypeFont font;

   @Override
   public void renderWidget() {
      if (font == null) {
         init();
      }

      // Render component repartition gui
      float height = 20;
      float length = 99;

      TextureImpl.bindNone();
      GL11.glColor4f(1, 1, 1, 1);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(0, 25);
      GL11.glVertex2f(length + 1, 25);
      GL11.glVertex2f(length + 1, 25 + height);
      GL11.glVertex2f(0, 25 + height);
      GL11.glEnd();

      float aggregatedPonderation = 0;
      int i = 0;
      Set<Entry<ComponentType, Float>> ponderations = uiContext.getSelectedShip().getComponentsComposition().entrySet();
      for (Entry<ComponentType, Float> ponderation : ponderations) {
         Component cmp = uiContext.getSelectedShip().getComponents().get(ponderation.getKey());

         RenderUtils.renderLine(new Vector2f(i * length / ponderations.size() + length / ponderations.size() / 2, 20),
               new Vector2f(aggregatedPonderation * length + length * ponderation.getValue() / 2, 27), 1, 1, new float[] { 1, 1,
            1, 0.25f }, new float[] { 0, 0, 0, 0 });

         GL11.glTranslatef(i * length / ponderations.size() + length / ponderations.size() / 2, 0, 0);
         // Texture texture = cmpTextures.get(ponderation.getKey());
         String str = ponderation.getKey().name();
         CharSequence ss = str.subSequence(0, 1);
         Color color = new Color(Color.white);
         RenderUtils.renderText(font, -(float) font.getWidth(ss) / 2, (float) font.getHeight(ss) / 2, ss.toString(), 1, color);
         GL11.glTranslatef(-i * length / ponderations.size() - length / ponderations.size() / 2, 0, 0);

         TextureImpl.bindNone();
         GL11.glTranslatef(aggregatedPonderation * length, 25, 0);
         float[] cmpColor = ponderation.getKey().getColor();
         if (!cmp.isActive()) {
            cmpColor = new float[] { 0.3f, 0.3f, 0.3f, 1 };
         }
         GL11.glColor4f(cmpColor[0], cmpColor[1], cmpColor[2], cmpColor[3]);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glVertex2f(1, 1);
         GL11.glVertex2f(length * ponderation.getValue(), 1);
         GL11.glVertex2f(length * ponderation.getValue(), height - 1);
         GL11.glVertex2f(1, height - 1);
         GL11.glEnd();
         GL11.glTranslatef(-aggregatedPonderation * length, -25, 0);

         aggregatedPonderation += ponderation.getValue();
         i++;
      }
   }

   private void init() {

      Font awtFont;
      try {
         awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(conf.getProperty("ship.renderer.font"))); //$NON-NLS-1$
         awtFont = awtFont.deriveFont(12f); // set font size
         font = new TrueTypeFont(awtFont, true);
      } catch (FontFormatException | IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
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

}
