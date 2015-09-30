package net.carmgate.morph.ui.renderers.entities.ship;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class ShipSelectRenderer implements SelectRenderer<Ship> {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;

	@Override
	public void render(Ship ship, float alpha) {
		final float massScale = ship.getMass();
		final float width = 128;

		GL11.glScalef(massScale, massScale, 1);
		GL11.glColor4f(1, 1, 1, 0.3f);
		GL11.glPushName(SelectRenderer.TargetType.SHIP.ordinal());
		GL11.glPushName(ship.getId());
		renderUtils.renderDisc(width / 2f);
		GL11.glPopName();
		GL11.glPopName();
		GL11.glScalef(1f / massScale, 1f / massScale, 1);
	}

}
