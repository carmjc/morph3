package net.carmgate.morph.ui.widgets.radar;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;

public class RadarWidget extends Widget {

	@Inject private RenderUtils renderUtils;
	@Inject private World world;

	@Override
	public void renderInteractiveAreas() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderWidget() {
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

		Vector2f toShip = new Vector2f();
		for (Ship ship : world.getShips()) {
			if (ship != playerShip) {
				float distanceToShipSq = ship.getPos().distanceToSquared(playerShip.getPos());
				if (distanceToShipSq < appearanceDistanceSq) {
					float ratio = 1;
					if (distanceToShipSq > interactionDistanceSq) {
						ratio = (appearanceDistanceSq - distanceToShipSq) / (appearanceDistanceSq - interactionDistanceSq);
					}
					toShip.copy(ship.getPos()).sub(playerShip.getPos());
					toShip.scale(getWidth() / 2 / radarRadius);
					GL11.glTranslatef(toShip.x, toShip.y, 0);
					renderUtils.renderCircle(0, 2, 0, 2, colorExt, new float[] { 1, 0.3f, 0.3f, ratio }, colorExt);
					GL11.glTranslatef(-toShip.x, -toShip.y, 0);
				}
			}
		}

		GL11.glTranslatef(-getWidth() / 2, -getHeight() / 2, 0);
	}

}
