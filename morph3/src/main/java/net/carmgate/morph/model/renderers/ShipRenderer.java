package net.carmgate.morph.model.renderers;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.Model;
import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.renderers.events.NewRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

	// Used for drawing circles efficiently
	private static final int nbSegments = 200;
	private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
	private static final float cos = (float) Math.cos(deltaAngle);
	private static final float sin = (float) Math.sin(deltaAngle);

	private static final Logger LOGGER = LoggerFactory.getLogger(ShipRenderer.class);

	/** The texture under the morph image. */
	private static Texture baseTexture;

	@Inject
	private Event<NewRenderer> event;

	@Inject
	private Model model;

	@PostConstruct
	private void fireEvent() {
		LOGGER.debug("Sending event");
		event.fire(new NewRenderer(this));
	}

	@Override
	public void init() {
		if (baseTexture == null) {
			try (FileInputStream fileInputStream = new FileInputStream(ClassLoader.getSystemResource("img/spaceship.png").getPath())) {
				baseTexture = TextureLoader.getTexture("PNG", fileInputStream);
			} catch (final IOException e) {
				LOGGER.error("Exception raised while loading texture", e);
			}
		}
	}

	@Override
	public void render(Ship ship, RenderMode mode) {

		// TODO
		final float mass = 2;
		final Vector2f pos = ship.getPos();
		// TODO

		final float massScale = mass / 10;
		final float width = 128;
		final float zoomFactor = 1;//model.getViewport().getZoomFactor();
		final boolean disappearZoom = massScale / mass * zoomFactor < 0.002f;
//		if (disappearZoom && !selected && getPlayer().getFof() != FOF.SELF) {
//			return;
//		}

		final boolean minZoom = massScale / mass * zoomFactor < 0.02f;

		// render trail
//		renderTrail(glMode);

		// Render behaviors
//		if (!isSelectRendering(glMode)) {
//			for (final Behavior behavior : getBehaviors()) {
//				if (behavior instanceof Renderable) {
//					((Renderable) behavior).render(glMode);
//				}
//			}
//		}

		GL11.glTranslatef(pos.x, pos.y, 0);
//		GL11.glRotatef(heading, 0, 0, 1);

		// Render selection circle around the ship
//		renderSelection(glMode, massScale, minZoom);

		// Render the ship in itself
//		if (Model.getModel().getUiContext().isDebugMode()) {
//			// IMPROVE replace this with some more proper mass rendering
//			final float energyPercent = energy / 100;
//			if (energyPercent <= 0) {
//				GL11.glColor3f(0.1f, 0.1f, 0.1f);
//			} else {
//				GL11.glColor3f(1f - energyPercent, energyPercent, 0);
//			}
//		} else {
			GL11.glColor3f(1f, 1f, 1f);
//		}
//		if (minZoom && (selected || getPlayer().getFof() == FOF.SELF)) {
//			GL11.glScalef(1f / (4 * zoomFactor), 1f / (4 * zoomFactor), 0);
//			if (isSelectRendering(glMode)) {
//				TextureImpl.bindNone();
//				RenderUtils.renderDisc(width / 2);
//			} else {
//				zoomedOutTexture.bind();
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glTexCoord2f(0, 0);
//				GL11.glVertex2f(-width / 2, -width / 2);
//				GL11.glTexCoord2f(1, 0);
//				GL11.glVertex2f(width / 2, -width / 2);
//				GL11.glTexCoord2f(1, 1);
//				GL11.glVertex2f(width / 2, width / 2);
//				GL11.glTexCoord2f(0, 1);
//				GL11.glVertex2f(-width / 2, width / 2);
//				GL11.glEnd();
//			}
//			GL11.glScalef(4 * zoomFactor, 4 * zoomFactor, 0);
//		} else {
//			if (isSelectRendering(glMode)) {
//				massScale = (float) Math.max(massScale, 0.02 * mass / zoomFactor);
//			}

			GL11.glScalef(massScale, massScale, 0);
//			if (isSelectRendering(glMode)) {
//				TextureImpl.bindNone();
//				RenderUtils.renderDisc(width / 2);
//			} else {
				baseTexture.bind();
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(-width / 2, -width / 2);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex2f(width / 2, -width / 2);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex2f(width / 2, width / 2);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(-width / 2, width / 2);
				GL11.glEnd();
//			}
			GL11.glScalef(1f / massScale, 1f / massScale, 0);
//		}

//		GL11.glRotatef(-heading, 0, 0, 1);
	}

}
