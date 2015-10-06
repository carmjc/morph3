package net.carmgate.morph.ui.renderers.animations.ship;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.model.animations.ship.MiningLaserAnim;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

public class MiningLaserRenderer implements Renderer<MiningLaserAnim> {

	private Random rand = new Random();

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(MiningLaserAnim laser, float alpha, FloatBuffer vpFb) {
		if (laser.getSource() != null && laser.getTarget() != null) {
			renderUtils.renderLine(laser.getSource().getShip().getPos(), laser.getTarget().getPos(), 10f, 0f, rand.nextFloat() * 5 + 3,
					new float[] { 1f, 1f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
		}
	}

}
