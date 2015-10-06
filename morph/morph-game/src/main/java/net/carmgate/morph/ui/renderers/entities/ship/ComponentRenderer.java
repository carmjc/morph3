package net.carmgate.morph.ui.renderers.entities.ship;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.components.repair.SimpleRepairer;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class ComponentRenderer implements Renderer<Component> {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;
	@Inject private Conf conf;
	@Inject private World world;
	@Inject private ComponentManager componentManager;

	private Map<ComponentType, Texture> cmpTextures = new HashMap<>();

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		try (BufferedInputStream laserInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
				conf.getProperty(Laser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream mlInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(MiningLaser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream repairerInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimpleRepairer.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream propInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimplePropulsor.class.getCanonicalName() + ".renderer.texture")))) {
			cmpTextures.put(ComponentType.LASERS, renderUtils.getTexture("PNG", laserInputStream));
			cmpTextures.put(ComponentType.MINING_LASERS, renderUtils.getTexture("PNG", mlInputStream));
			cmpTextures.put(ComponentType.REPAIRER, renderUtils.getTexture("PNG", repairerInputStream));
			cmpTextures.put(ComponentType.PROPULSORS, renderUtils.getTexture("PNG", propInputStream));
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
		}
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Component cmp, float alpha, FloatBuffer vpFb) {
		final float width = 50;
		final Ship ship = cmp.getShip();

		// draw the component
		Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());

		if (componentManager.getAvailability(cmp) < 1 && ship == world.getPlayerShip()) {
			GL11.glRotatef(-ship.getRotation() - 90, 0, 0, 1);
			renderUtils.renderAntialiasedPartialDisc(0 + componentManager.getAvailability(cmp), 1, width / 2 - 10,
					new float[] { 0.3f, 0.3f, 0.3f, 0.8f * alpha }, 1);
			GL11.glRotatef(ship.getRotation() + 90, 0, 0, 1);
		}

		Color color = new Color(Color.white);
		GL11.glColor4f(color.r, color.g, color.b, color.a * alpha);
		if (texture != null) {
			renderUtils.renderSprite(width, texture);
		}

	}

}
