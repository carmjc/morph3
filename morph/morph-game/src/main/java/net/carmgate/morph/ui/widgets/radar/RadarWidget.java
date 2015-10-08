package net.carmgate.morph.ui.widgets.radar;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.GeoUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;

public class RadarWidget extends Widget {

	@Inject private RenderUtils renderUtils;
	@Inject private MWorld world;

	@Override
	public void renderInteractiveAreas() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		Ship playerShip = world.getPlayerShip();
		float radarRadius = playerShip.getPerceptionRadius();
		float outerRadius = radarRadius / 8;
		float interactionDistanceSq = radarRadius * radarRadius;
		float appearanceDistanceSq = (radarRadius + outerRadius) * (radarRadius + outerRadius);

		// draw reticule
		float[] colorInt = new float[] { 0.5f, 0.5f, 0.5f, 1 };
		float[] colorExt = new float[] { 0, 0, 0, 0 };
		GL11.glTranslatef(getWidth() / 2, getHeight() / 2, 0);
		for (float radius = getWidth() / 2; radius > 0; radius -= 27) {
			renderUtils.renderCircle(radius, radius, 2, 2, colorExt, colorInt, colorExt);
		}

		Vec2 toShip = new Vec2();
		for (Ship ship : world.getShips()) {
			if (ship != playerShip) {
				float distanceToShipSq = GeoUtils.distanceToSquared(ship.getPosition(), playerShip.getPosition());
				if (distanceToShipSq < appearanceDistanceSq) {
					float ratio = 1;
					if (distanceToShipSq > interactionDistanceSq) {
						ratio = (appearanceDistanceSq - distanceToShipSq) / (appearanceDistanceSq - interactionDistanceSq);
					}
					toShip.set(ship.getPosition()).sub(playerShip.getPosition());
					toShip.mul(getWidth() / 2 / radarRadius);
					GL11.glTranslatef(toShip.x, toShip.y, 0);
					renderUtils.renderCircle(0, 2, 0, 2, colorExt, new float[] { 1, 0.3f, 0.3f, ratio }, colorExt);
					GL11.glTranslatef(-toShip.x, -toShip.y, 0);
				}
			}
		}

		GL11.glTranslatef(-getWidth() / 2, -getHeight() / 2, 0);
	}

}
