package net.carmgate.morph.ui.renderers.entities.ship;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class ShipSelectRenderer implements SelectRenderer<Ship> {

	@Inject private Logger LOGGER;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
		newRendererEventMgr.fire(new NewRendererFound(this));
	}

	@Override
	public void render(Ship ship) {
		LOGGER.debug("tap");

		final float massScale = ship.getMass();
		final float width = 256;

		GL11.glScalef(massScale, massScale, 1);
		RenderUtils.renderDisc(width / 2f);
		GL11.glScalef(1f / massScale, 1f / massScale, 1);
	}

}
