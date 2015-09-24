package net.carmgate.morph.ui.renderers;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.particles.Particle;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class ParticleRenderer implements Renderer<Particle> {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;
	@Inject private Conf conf;
	@Inject private UIContext uiContext;
	@Inject private World world;

	private Texture particleTexture;

	@Override
	public void init() {
		try (BufferedInputStream textureInputStream = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(conf.getProperty("particle.texture")))) { //$NON-NLS-1$
			particleTexture = renderUtils.getTexture("PNG", textureInputStream);
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
		}
	}

	@Override
	public void render(Particle p, float alpha) {
		final float zoomFactor = uiContext.getViewport().getZoomFactor();
		long timeToLive = p.getDeathTime() - p.getBirthTime();
		long timeLived = world.getTime() - p.getBirthTime();
		alpha = alpha * Math.min(1, 5f * (timeToLive - timeLived) / timeToLive);
		GL11.glColor4f(1, 1, 1, alpha);
		renderUtils.renderSprite(20 / zoomFactor, particleTexture);
	}

}
