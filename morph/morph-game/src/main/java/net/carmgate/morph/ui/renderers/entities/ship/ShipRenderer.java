package net.carmgate.morph.ui.renderers.entities.ship;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
import net.carmgate.morph.managers.ComponentManager;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.components.repair.SimpleRepairer;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.actions.DragContext;
import net.carmgate.morph.ui.actions.DragContext.DragType;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.TextAlign;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

	private static Map<ComponentType, Texture> cmpTextures = new HashMap<>();
	// private static Texture shipBgTexture;
	private static Texture shipTexture;

	@Inject private UIContext uiContext;
	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;
	@Inject private DragContext dragContext;
	@Inject private RenderUtils renderUtils;
	@Inject private ComponentManager componentManager;

	@Override
	public void init() {
		try (BufferedInputStream shipBgInputStream = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture.bg"))); //$NON-NLS-1$
				BufferedInputStream shipInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty("ship.renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream laserInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(Laser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream mlInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(MiningLaser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream repairerInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimpleRepairer.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream propInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimplePropulsor.class.getCanonicalName() + ".renderer.texture")))) { //$NON-NLS-1$
			// shipBgTexture = renderUtils.getTexture("PNG", shipBgInputStream);
			shipTexture = renderUtils.getTexture("PNG", shipInputStream);
			cmpTextures.put(ComponentType.LASERS, renderUtils.getTexture("PNG", laserInputStream));
			cmpTextures.put(ComponentType.MINING_LASERS, renderUtils.getTexture("PNG", mlInputStream));
			cmpTextures.put(ComponentType.REPAIRER, renderUtils.getTexture("PNG", repairerInputStream));
			cmpTextures.put(ComponentType.PROPULSORS, renderUtils.getTexture("PNG", propInputStream));
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
		GL11.glRotatef(ship.getRotation(), 0, 0, 1);

		// FIXME #23
		if (ship == uiContext.getSelectedShip()) {
			float colorScale = (int) (world.getTime() % 1000);
			colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
			renderUtils.renderCircle(width / 2f - 0 / massScale,
					width / 2f - 0 / massScale,
					2 / zoom / massScale,
					5 / zoom / massScale,
					new float[] { 0f, 0f, 0f, 0f },
					new float[] { 1f, 1f, 1f, 0.5f * colorScale * alpha },
					new float[] { 0f, 0f, 0f, 0f });
		}

		float[] color = ship.getPlayer().getColor();
		// GL11.glColor4f(color[0] / 4, color[1] / 4, color[2] / 4, color[3] * alpha);
		// renderUtils.renderSprite(width, shipBgTexture);
		GL11.glColor4f(color[0], color[1], color[2], color[3] * alpha);
		float skewRatio = 1f;
		if (ship.getRotationSpeed() > 0) {
			skewRatio = 0.9f;
		} else if (ship.getRotationSpeed() < 0) {
			skewRatio = -0.9f;
		}

		renderUtils.renderSprite(width, shipTexture, skewRatio);
		renderComponents(ship, alpha);

		GL11.glRotatef(-ship.getRotation(), 0, 0, 1);

		// Render energy and resource gauges
		float delta = Math.max(30, 30 / zoom);
		float thickness = Math.max(4, 4 / zoom);
		float blurriness = 6 / zoom;
		float offset = 0.2f; // must be inferior to 0.25
		if (ship == uiContext.getSelectedShip()) {
			// Energy
			if (ship.getEnergyMax() > 0) {
				renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
						width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
						new float[] { 0, 0, 0, 0f },
						new float[] { 0.6f, 0.6f, 0.6f, 1 },
						new float[] { 0, 0, 0, 0f });
				renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getEnergy() / ship.getEnergyMax(),
						width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
						new float[] { 0, 0, 0, 0f },
						new float[] { 0, 0.5f, 1, 1 },
						new float[] { 0, 0, 0, 0f });
			}

			// Resources
			if (ship.getResourcesMax() > 0) {
				GL11.glScalef(-1, 1, 1);
				renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
						width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
						new float[] { 0, 0, 0, 0f },
						new float[] { 0.6f, 0.6f, 0.6f, 1 },
						new float[] { 0, 0, 0, 0f });
				renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getResources() / ship.getResourcesMax(),
						width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
						new float[] { 0, 0, 0, 0f },
						new float[] { 139f / 255, 90f / 255, 43f / 255, 1 },
						new float[] { 0, 0, 0, 0f });
				GL11.glScalef(-1, 1, 1);
			}

			// Integrity
			GL11.glRotatef(90, 0, 0, 1);
			GL11.glScalef(-1, 1, 1);
			renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
					width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
					new float[] { 0, 0, 0, 0f },
					new float[] { 0.6f, 0.6f, 0.6f, 1 },
					new float[] { 0, 0, 0, 0f });
			renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getIntegrity(),
					width / 2f + delta - thickness, width / 2f + delta + thickness, blurriness, blurriness,
					new float[] { 0, 0, 0, 0f },
					new float[] { 0.8f, 0.0f, 0.0f, 1 },
					new float[] { 0, 0, 0, 0f });
			GL11.glScalef(-1, 1, 1);
			GL11.glRotatef(-90, 0, 0, 1);
		}

		GL11.glScalef(1f / massScale, 1f / massScale, 0);

		if (ship == uiContext.getSelectedShip()) {
			GL11.glTranslatef(0, -width / 4f, 0);
			renderUtils.renderText(RenderingManager.font, "XP: " + ship.getXp(), 0, Color.white, TextAlign.CENTER);
			GL11.glTranslatef(0, width / 4f, 0);
		}

		// draw component range
		Component selectedCmp = uiContext.getSelectedCmp();
		if (selectedCmp != null && selectedCmp.getShip() == ship && selectedCmp.getRange() != 0 && dragContext.dragInProgress(DragType.COMPONENT)) {
			float[] cmpColor = selectedCmp.getColor();
			if (cmpColor == null) {
				cmpColor = new float[] { 1, 1, 1, 0.4f };
			}
			renderRange(selectedCmp, cmpColor, true);
			if (selectedCmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.PROPULSORS
					&& selectedCmp.getTargetPosInWorld() != null) {
				GL11.glTranslatef(-ship.getPos().x + selectedCmp.getTargetPosInWorld().x, -ship.getPos().y + selectedCmp.getTargetPosInWorld().y, 0);

				for (Component cmp : ship.getComponents().values()) {
					if (cmp != selectedCmp && cmp.getClass().isAnnotationPresent(NeedsTarget.class)) {
						cmpColor = cmp.getColor();
						if (cmpColor == null) {
							cmpColor = new float[] { 1, 1, 1, 0.4f };
						}
						renderRange(cmp, cmpColor, false);
					}
				}
				GL11.glTranslatef(ship.getPos().x - selectedCmp.getTargetPosInWorld().x, ship.getPos().y - selectedCmp.getTargetPosInWorld().y, 0);
			}
		}

		if (selectedCmp != null && selectedCmp.getShip() == ship && selectedCmp.getRange() != 0) {
			// draw component target selection
			Vector2f targetPos = selectedCmp.getTargetPosInWorld();
			if (targetPos != null) {
				float radius = 20 / zoom;
				PhysicalEntity target = selectedCmp.getTarget();
				if (target != null) {
					if (target instanceof Ship) {
						radius = ((Ship) target).getMass() * 150;
					}
				}

				GL11.glTranslatef(targetPos.x - ship.getPos().x, targetPos.y - ship.getPos().y, 0);
				float blur = 2 + new Random().nextFloat();
				renderCircling(radius + blur, blur / zoom, (int) (radius / 4), new float[] { 1f, 0.5f, 0.5f, 1 });
				GL11.glTranslatef(-targetPos.x + ship.getPos().x, -targetPos.y + ship.getPos().y, 0);
			}

		}

		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			// Accel
			Vector2f accel = new Vector2f(ship.getAccel());
			renderUtils.renderLine(Vector2f.NULL, accel, 2, 2, new float[] { 1f, 0f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			// Speed
			Vector2f speed = new Vector2f(ship.getSpeed());
			renderUtils.renderLine(Vector2f.NULL, speed, 2, 2, new float[] { 0f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });

			renderUtils.renderLine(Vector2f.NULL, ship.debug1, 2, 2, new float[] { 0f, 0f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			renderUtils.renderLine(Vector2f.NULL, ship.debug2, 2, 2, new float[] { 0.5f, 0.5f, 0.5f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			renderUtils.renderLine(Vector2f.NULL, ship.debug3, 2, 2, new float[] { 1f, 1f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
			renderUtils.renderLine(Vector2f.NULL, ship.debug4, 2, 2, new float[] { 1f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		}

	}

	private void renderCircling(float radius, float blur, int nbSegments, float[] color) {
		float timeAngle;
		timeAngle = (float) (world.getAbsoluteTime() % (5000 * nbSegments)) / (5000 * nbSegments) * 360;
		GL11.glRotatef(timeAngle, 0, 0, 1);
		renderUtils.renderSegmentedCircle(
				radius,
				radius,
				blur,
				blur,
				new float[] { color[0], color[1], color[2], 0 },
				new float[] { color[0], color[1], color[2], color[3] },
				new float[] { color[0], color[1], color[2], 0 },
				nbSegments);
		renderUtils.renderAntialiasedDisc(radius, blur, new float[] { color[0], color[1], color[2], color[3] * 0.1f });
		GL11.glRotatef(-timeAngle, 0, 0, 1);
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
		GL11.glScalef(1 / Component.SCALE, 1 / Component.SCALE, 1);

		for (Component cmp : components) {
			if (cmp.getPosInShip().isNull()) {
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
				cmp.getPosInShip().copy(compX, compY);
			}
			GL11.glTranslatef(cmp.getPosInShip().x, cmp.getPosInShip().y, zoom);

			// draw the component
			Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());
			Color color = new Color(Color.white);
			if (cmp == uiContext.getSelectedCmp()) {
				float colorScale = (int) (world.getTime() % 1000);
				colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
				renderUtils.renderCircle(width / 2f + 10,
						width / 2f + 10,
						40 / zoom,
						50 / zoom,
						new float[] { 0f, 0f, 0f, 0f },
						new float[] { 1f, 1f, 1f, 0.5f * colorScale * alpha },
						new float[] { 0f, 0f, 0f, 0f });
			}

			GL11.glColor4f(color.r, color.g, color.b, color.a * alpha);
			if (texture != null) {
				renderUtils.renderSprite(width, texture);
			}

			if (componentManager.getAvailability(cmp) < 1) {
				GL11.glColor4f(0, 0, 0, 0.8f);
				GL11.glRotatef(-ship.getRotation() - 90, 0, 0, 1);
				renderUtils.renderAntialiasedPartialDisc(0 + componentManager.getAvailability(cmp), 1, width / 2 - 20, new float[] { 0, 0, 0, 0.8f * alpha },
						zoom);
				GL11.glRotatef(ship.getRotation() + 90, 0, 0, 1);
			}

			GL11.glTranslatef(-cmp.getPosInShip().x, -cmp.getPosInShip().y, zoom);
		}

		GL11.glScalef(Component.SCALE, Component.SCALE, 1);

	}

	private void renderRange(Component selectedCmp, float[] color, boolean withLine) {
		float zoom = uiContext.getViewport().getZoomFactor();
		float blur = 2 + new Random().nextFloat();
		int nbSegments = (int) (selectedCmp.getRange() / 10);
		float timeAngle = (float) (world.getAbsoluteTime() % (5000 * nbSegments)) / (5000 * nbSegments) * 360;
		renderCircling(selectedCmp.getRange() + blur, blur / zoom, (int) (selectedCmp.getRange() / 10), color);

		if (withLine) {
			Vector2f from = new Vector2f(selectedCmp.getPosInShip()).scale(selectedCmp.getShip().getMass() / Component.SCALE)
					.rotate(selectedCmp.getShip().getRotation());
			Vector2f to = new Vector2f(0, selectedCmp.getRange() + blur).rotate(timeAngle - 90 / nbSegments);
			Vector2f vect = new Vector2f(to).sub(from);
			vect.scale((128 / Component.SCALE + 2 / zoom) / vect.length());
			from.add(vect);
			renderUtils.renderLine(from, to, 0, blur / zoom, color, new float[] { 0, 0, 0, 0 });
		}
	}

}
