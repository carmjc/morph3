package net.carmgate.morph.ui.widgets;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.actions.DragContext;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

@Singleton
public class ComponentPonderationWidget extends Widget implements WidgetMouseListener {

   @Inject private UIContext uiContext;
   @Inject private Logger LOGGER;
   @Inject private Conf conf;
   @Inject private GameMouse gameMouse;
   @Inject private DragContext dragContext;

   private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
   private static TrueTypeFont font;
   private ComponentType selectedCmpType;
   private ComponentType otherCmpType;
   private boolean leftAnchor;
   private float startOtherCmpPercentage;
   private float startSelectedCmpPercentage;

   @Override
   public void renderWidget() {
      if (font == null) {
         init();
      }

      if (uiContext.getSelectedShip() == null) {
         return;
      }

      // Render component repartition gui
      float height = conf.getFloatProperty("ui.componentPonderationWidget.height"); //$NON-NLS-1$
      float width = conf.getFloatProperty("ui.componentPonderationWidget.width"); //$NON-NLS-1$

      TextureImpl.bindNone();
      GL11.glColor4f(1, 1, 1, 1);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(0, 25);
      GL11.glVertex2f(width + 1, 25);
      GL11.glVertex2f(width + 1, 25 + height);
      GL11.glVertex2f(0, 25 + height);
      GL11.glEnd();

      float aggregatedPonderation = 0;
      int i = 0;
      int nbCmpType = uiContext.getSelectedShip().getComponentsComposition().entrySet().size();
      for (Entry<ComponentType, Float> ponderation : uiContext.getSelectedShip().getComponentsComposition().entrySet()) {
         Component cmp = uiContext.getSelectedShip().getComponents().get(ponderation.getKey());

         RenderUtils.renderLine(new Vector2f(i * width / nbCmpType + width / nbCmpType / 2, 20),
               new Vector2f(aggregatedPonderation * width + width * ponderation.getValue() / 2, 27), 1, 1, new float[] { 1, 1,
            1, 0.25f }, new float[] { 0, 0, 0, 0 });

         GL11.glTranslatef(i * width / nbCmpType + width / nbCmpType / 2, 0, 0);
         // Texture texture = cmpTextures.get(ponderation.getKey());
         String str = ponderation.getKey().name();
         CharSequence ss = str.subSequence(0, 1);
         Color color = new Color(Color.white);
         RenderUtils.renderText(font, -(float) font.getWidth(ss) / 2, (float) font.getHeight(ss) / 2, ss.toString(), 1, color);
         GL11.glTranslatef(-i * width / nbCmpType - width / nbCmpType / 2, 0, 0);

         TextureImpl.bindNone();
         GL11.glTranslatef(aggregatedPonderation * width, 25, 0);
         float[] cmpColor = ponderation.getKey().getColor();
         if (!cmp.isActive()) {
            cmpColor = new float[] { 0.3f, 0.3f, 0.3f, 1 };
         }
         GL11.glColor4f(cmpColor[0], cmpColor[1], cmpColor[2], cmpColor[3]);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glVertex2f(1, 1);
         GL11.glVertex2f(width * ponderation.getValue(), 1);
         GL11.glVertex2f(width * ponderation.getValue(), height - 1);
         GL11.glVertex2f(1, height - 1);
         GL11.glEnd();
         GL11.glTranslatef(-aggregatedPonderation * width, -25, 0);

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

   @Override
   public void renderInteractiveAreas() {
      if (uiContext.getSelectedShip() == null) {
         return;
      }

      // Render component repartition gui
      float height = conf.getFloatProperty("ui.componentPonderationWidget.height"); //$NON-NLS-1$
      float width = conf.getFloatProperty("ui.componentPonderationWidget.width"); //$NON-NLS-1$

      TextureImpl.bindNone();
      GL11.glColor4f(1, 1, 1, 1);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(0, 25);
      GL11.glVertex2f(width + 1, 25);
      GL11.glVertex2f(width + 1, 25 + height);
      GL11.glVertex2f(0, 25 + height);
      GL11.glEnd();
   }

   @Override
   public void onDrag() {
      if (dragContext.dragInProgress()) {
         Float dragStart = getCoeff(dragContext.getOldMousePosInWindow().x);
         float drag = getCoeff(gameMouse.getX());
         LOGGER.debug("Old: " + dragStart + " - New: " + drag);

         Ship ship = uiContext.getSelectedShip();
         if (selectedCmpType == null) {
            leftAnchor = false;

            // Find anchor
            otherCmpType = null;
            float cmpStart = 0;
            float cmpEnd = 0;
            for (Entry<ComponentType, Float> entry : ship.getComponentsComposition().entrySet()) {
               if (leftAnchor) {
                  otherCmpType = entry.getKey();
                  startOtherCmpPercentage = ship.getComponentsComposition().get(otherCmpType);
                  startSelectedCmpPercentage = ship.getComponentsComposition().get(selectedCmpType);
                  LOGGER.debug("selectedCmp: " + selectedCmpType + " - otherCmpType: " + otherCmpType);
                  return;
               }

               float cmpWidth = entry.getValue();
               cmpEnd = cmpStart + cmpWidth;
               if (dragStart >= cmpStart && dragStart < cmpStart + Math.min(10 / conf.getFloatProperty("ui.componentPonderationWidget.width"), cmpWidth)) {
                  selectedCmpType = entry.getKey();
                  if (otherCmpType != null) {
                     startOtherCmpPercentage = ship.getComponentsComposition().get(otherCmpType);
                     startSelectedCmpPercentage = ship.getComponentsComposition().get(selectedCmpType);
                     LOGGER.debug("selectedCmp: " + selectedCmpType + " - otherCmpType: " + otherCmpType);
                     return;
                  }
               }
               if (dragStart < cmpEnd && dragStart >= cmpEnd - Math.min(cmpWidth, 10 / conf.getFloatProperty("ui.componentPonderationWidget.width"))) {
                  selectedCmpType = entry.getKey();
                  leftAnchor = true;
               }

               cmpStart += cmpWidth;
               if (selectedCmpType == null) {
                  otherCmpType = entry.getKey();
               }
            }

            // if we have not returned yet, this is because we are in an incompatible situation
            selectedCmpType = null;
            otherCmpType = null;
         } else {
            float delta = (gameMouse.getX() - dragContext.getOldMousePosInWindow().getX()) / conf.getFloatProperty("ui.componentPonderationWidget.width");
            if (leftAnchor) {
               ship.getComponentsComposition().put(otherCmpType, Math.min(Math.max(0f, startOtherCmpPercentage - delta), startOtherCmpPercentage + startSelectedCmpPercentage));
               ship.getComponentsComposition().put(selectedCmpType, Math.min(Math.max(0f, startSelectedCmpPercentage + delta), startOtherCmpPercentage + startSelectedCmpPercentage));
            } else {
               ship.getComponentsComposition().put(otherCmpType, Math.min(Math.max(0f, startOtherCmpPercentage + delta), startOtherCmpPercentage + startSelectedCmpPercentage));
               ship.getComponentsComposition().put(selectedCmpType, Math.min(Math.max(0f, startSelectedCmpPercentage - delta), startOtherCmpPercentage + startSelectedCmpPercentage));
            }
         }
      }

      if (!dragContext.dragInProgress()) {
         selectedCmpType = null;
         otherCmpType = null;
      }
   }

   public float getCoeff(float oldMouseX) {
      float dragStart = (oldMouseX - 5) / conf.getFloatProperty("ui.componentPonderationWidget.width");
      if (dragStart < 0) {
         dragStart = 0;
      }
      if (dragStart > 1) {
         dragStart = 1;
      }
      return dragStart;
   }

}
