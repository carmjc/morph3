package net.carmgate.morph.ui.renderers.animations.ship;

import java.util.Random;

import javax.inject.Inject;

import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

public class LaserRenderer implements Renderer<LaserAnim> {

	@Inject private RenderUtils renderUtils;

	private Random rand = new Random();

	@Override
	public void render(LaserAnim laserAnim, float alpha) {
		if (laserAnim.getSource() != null && laserAnim.getTarget() != null) {
			renderUtils.renderLine(laserAnim.getSource().getPos(), laserAnim.getTarget().getPos(), 2f, 1f, rand.nextFloat() * 1 + 3,
					new float[] { 1f, 0f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
		}
	}

}
