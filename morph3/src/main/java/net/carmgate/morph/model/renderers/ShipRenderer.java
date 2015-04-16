package net.carmgate.morph.model.renderers;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.UIContext;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

   // Used for drawing circles efficiently
   // private static final int nbSegments = 36;
   // private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
   // private static final float cos = (float) Math.cos(deltaAngle);
   // private static final float sin = (float) Math.sin(deltaAngle);

   /** The texture under the morph image. */
   // private static Texture baseShipTexture;

   @Inject
   private UIContext uiContext;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      // if (baseShipTexture == null) {
      // try (FileInputStream fileInputStream = new FileInputStream(ClassLoader.getSystemResource("img/circle128x10.png").getPath())) {
      // baseShipTexture = TextureLoader.getTexture("PNG", fileInputStream);
      // } catch (final IOException e) {
      // LOGGER.error("Exception raised while loading texture", e);
      // }
      // }
   }

   @Override
   public void render(Ship ship) {

      // TODO
      final float mass = 10;
      // final Vector2f pos = ship.getPos();
      // TODO

      final float massScale = mass / 10 * uiContext.getZoom();
      final float width = 128;
      // final boolean disappearZoom = massScale / mass * zoomFactor < 0.002f;
      // if (disappearZoom && !selected && getPlayer().getFof() != FOF.SELF) {
      // return;
      // }

      // final boolean minZoom = massScale / mass * zoomFactor < 0.02f;

      // render trail
      // renderTrail(glMode);

      // Render behaviors
      // if (!isSelectRendering(glMode)) {
      // for (final Behavior behavior : getBehaviors()) {
      // if (behavior instanceof Renderable) {
      // ((Renderable) behavior).render(glMode);
      // }
      // }
      // }

      // GL11.glTranslatef(pos.x, pos.y, 0);
      // GL11.glRotatef(heading, 0, 0, 1);

      // Render selection circle around the ship
      // renderSelection(glMode, massScale, minZoom);

      // Render the ship in itself
      // if (Model.getModel().getUiContext().isDebugMode()) {
      // // IMPROVE replace this with some more proper mass rendering
      // final float energyPercent = energy / 100;
      // if (energyPercent <= 0) {
      // GL11.glColor3f(0.1f, 0.1f, 0.1f);
      // } else {
      // GL11.glColor3f(1f - energyPercent, energyPercent, 0);
      // }
      // } else {
      // GL11.glColor3f(1f, 1f, 1f);
      // }
      // if (minZoom && (selected || getPlayer().getFof() == FOF.SELF)) {
      // GL11.glScalef(1f / (4 * zoomFactor), 1f / (4 * zoomFactor), 0);
      // if (isSelectRendering(glMode)) {
      // TextureImpl.bindNone();
      // RenderUtils.renderDisc(width / 2);
      // } else {
      // zoomedOutTexture.bind();
      // GL11.glBegin(GL11.GL_QUADS);
      // GL11.glTexCoord2f(0, 0);
      // GL11.glVertex2f(-width / 2, -width / 2);
      // GL11.glTexCoord2f(1, 0);
      // GL11.glVertex2f(width / 2, -width / 2);
      // GL11.glTexCoord2f(1, 1);
      // GL11.glVertex2f(width / 2, width / 2);
      // GL11.glTexCoord2f(0, 1);
      // GL11.glVertex2f(-width / 2, width / 2);
      // GL11.glEnd();
      // }
      // GL11.glScalef(4 * zoomFactor, 4 * zoomFactor, 0);
      // } else {
      // if (isSelectRendering(glMode)) {
      // massScale = (float) Math.max(massScale, 0.02 * mass / zoomFactor);
      // }

      GL11.glScalef(massScale, massScale, 0);
      // if (isSelectRendering(glMode)) {
      // TextureImpl.bindNone();
      // RenderUtils.renderDisc(width / 2);
      // } else {
      // baseShipTexture.bind();
      // GL11.glBegin(GL11.GL_QUADS);
      // GL11.glTexCoord2f(0, 0);
      // GL11.glVertex2f(-width / 2, -width / 2);
      // GL11.glTexCoord2f(1, 0);
      // GL11.glVertex2f(width / 2, -width / 2);
      // GL11.glTexCoord2f(1, 1);
      // GL11.glVertex2f(width / 2, width / 2);
      // GL11.glTexCoord2f(0, 1);
      // GL11.glVertex2f(-width / 2, width / 2);
      // GL11.glEnd();
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
      // }
      GL11.glScalef(1f / massScale, 1f / massScale, 0);
      // }

      // GL11.glRotatef(-heading, 0, 0, 1);
      // GL11.glTranslatef(-pos.x, -pos.y, 0);
   }
}
