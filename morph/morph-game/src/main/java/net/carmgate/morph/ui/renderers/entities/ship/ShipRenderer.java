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

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

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

@Singleton
public class ShipRenderer implements Renderer<Ship> {

	private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
	private static Texture shipBgTexture;
	private static Texture shipTexture;
	@Inject private UIContext uiContext;

	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;

	@Override
	public void init() {
		try (BufferedInputStream shipBgInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture.bg"))); //$NON-NLS-1$
				BufferedInputStream shipInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream laserInputStream = new BufferedInputStream(ClassLoader
						.getSystemResourceAsStream(conf.getProperty("net.carmgate.morph.model.entities.physical.ship.components.Laser.renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream mlInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty("net.carmgate.morph.model.entities.physical.ship.components.MiningLaser.renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream repairerInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("component.repairer.renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream propInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("net.carmgate.morph.model.entities.physical.ship.components.SimplePropulsor.renderer.texture")))) { //$NON-NLS-1$
			shipBgTexture = RenderUtils.getTexture("PNG", shipBgInputStream);
			shipTexture = RenderUtils.getTexture("PNG", shipInputStream);
			cmpTextures.put(ComponentType.LASERS, RenderUtils.getTexture("PNG", laserInputStream));
			cmpTextures.put(ComponentType.MINING_LASERS, RenderUtils.getTexture("PNG", mlInputStream));
			cmpTextures.put(ComponentType.REPAIRER, RenderUtils.getTexture("PNG", repairerInputStream));
			cmpTextures.put(ComponentType.PROPULSORS, RenderUtils.getTexture("PNG", propInputStream));
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
		newRendererEventMgr.fire(new NewRendererFound(this));
	}

	@Override
	public void render(Ship ship, float alpha) {

		final float massScale = ship.getMass();
		final float width = 256;
		float zoom = uiContext.getViewport().getZoomFactor();

		GL11.glScalef(massScale, massScale, 0);
		GL11.glColor4f(1f, 1f, 1f, 0.6f * alpha);
		GL11.glRotatef(ship.getRotate(), 0, 0, 1);

		// FIXME #23
		if (ship == uiContext.getSelectedShip()) {
			float colorScale = (int) (world.getTime() % 1000);
			colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
			RenderUtils.renderCircle(width / 2f - 0 / massScale,
					width / 2f - 0 / massScale,
					2 / zoom / massScale,
					5 / zoom / massScale,
					new float[] { 0f, 0f, 0f, 0f },
					new float[] { 1f, 1f, 1f, 0.5f * colorScale * alpha },
					new float[] { 0f, 0f, 0f, 0f });
		}

		float[] color = ship.getPlayer().getColor();
		GL11.glColor4f(color[0] / 4, color[1] / 4, color[2] / 4, color[3] * alpha);
		RenderUtils.renderSprite(width, shipBgTexture);
		GL11.glColor4f(color[0], color[1], color[2], color[3] * alpha);
		RenderUtils.renderSprite(width, shipTexture);

		renderComponents(ship, alpha);

		GL11.glRotatef(-ship.getRotate(), 0, 0, 1);
		GL11.glScalef(1f / massScale, 1f / massScale, 0);

		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			// Accel
			Vector2f accel = new Vector2f(ship.getAccel());
			RenderUtils.renderLine(Vector2f.NULL, accel, 2, 2, new float[] { 1f, 0f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			// Speed
			Vector2f speed = new Vector2f(ship.getSpeed());
			RenderUtils.renderLine(Vector2f.NULL, speed, 2, 2, new float[] { 0f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });

			RenderUtils.renderLine(Vector2f.NULL, ship.debug1, 2, 2, new float[] { 0f, 0f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			RenderUtils.renderLine(Vector2f.NULL, ship.debug2, 2, 2, new float[] { 0.5f, 0.5f, 0.5f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			RenderUtils.renderLine(Vector2f.NULL, ship.debug3, 2, 2, new float[] { 1f, 1f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			RenderUtils.renderLine(Vector2f.NULL, ship.debug4, 2, 2, new float[] { 1f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		}

	}

	/**
	 * @param ship
	 * @param massScale
	 * @param width
	 */
	private void renderComponents(Ship ship, float alpha) {
		float zoom = uiContext.getViewport().getZoomFactor();
		final float width = 512;

		Collection<Component> components = ship.getComponents().values();
		int propIndex = 0;
		int turretIndex = 0;
		int coreIndex = 0;
		int shipType = 0;
		float compScale = 3.292f * 2;
		GL11.glScalef(1 / compScale, 1 / compScale, 1);

		for (Component cmp : components) {
			float compX;
			float compY;
			if (cmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.PROPULSORS) {
				compX = conf.getFloatProperty("ship." + shipType + ".comps.prop." + propIndex + ".x");
				compY = conf.getFloatProperty("ship." + shipType + ".comps.prop." + propIndex + ".y");
				propIndex++;
			} else if (cmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.LASERS) {
				compX = conf.getFloatProperty("ship." + shipType + ".comps.turret." + turretIndex + ".x");
				compY = conf.getFloatProperty("ship." + shipType + ".comps.turret." + turretIndex + ".y");
				turretIndex++;
			} else {
				compX = conf.getFloatProperty("ship." + shipType + ".comps.core." + coreIndex + ".x");
				compY = conf.getFloatProperty("ship." + shipType + ".comps.core." + coreIndex + ".y");
				coreIndex++;
			}
			GL11.glTranslatef(compX, compY, zoom);

			// draw the component
			Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());
			Color color = new Color(Color.white);
			if (cmp == uiContext.getSelectedCmp()) {
				float colorScale = (int) (world.getTime() % 1000);
				colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
				RenderUtils.renderCircle(width / 2f + 10,
						width / 2f + 10,
						40 / zoom,
						50 / zoom,
						new float[] { 0f, 0f, 0f, 0f },
						new float[] { 1f, 1f, 1f, 0.5f * colorScale * alpha },
						new float[] { 0f, 0f, 0f, 0f });
			}

			GL11.glColor4f(color.r, color.g, color.b, color.a * alpha);
			if (texture != null) {
				RenderUtils.renderSprite(width, texture);
			}

			if (cmp.getAvailability() < 1) {
				GL11.glColor4f(0, 0, 0, 0.8f);
				GL11.glRotatef(-ship.getRotate() - 90, 0, 0, 1);
				RenderUtils.renderAntialiasedPartialDisc(0 + cmp.getAvailability(), 1, width / 2 - 20, new float[] { 0, 0, 0, 0.8f * alpha }, zoom);
				GL11.glRotatef(ship.getRotate() + 90, 0, 0, 1);
			}

			GL11.glTranslatef(-compX, -compY, zoom);
		}

		GL11.glScalef(compScale, compScale, 1);
	}

}
