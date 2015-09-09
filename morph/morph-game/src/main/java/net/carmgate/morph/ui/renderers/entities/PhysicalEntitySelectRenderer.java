package net.carmgate.morph.ui.renderers.entities;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class PhysicalEntitySelectRenderer implements SelectRenderer<PhysicalEntity> {

	@Inject private Logger LOGGER;

	@Inject	private Conf conf;
	@Inject private UIContext uiContext;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
		newRendererEventMgr.fire(new NewRendererFound(this));
	}

	@Override
	public void render(PhysicalEntity entity, float alpha) {
		final float massScale = entity.getMass() * conf.getFloatProperty("asteroid.renderer.massToSizeFactor");
		final float width = 128;

		GL11.glScalef(massScale, massScale, 1);
		GL11.glColor4f(1, 1, 1, 0.3f);
		GL11.glPushName(SelectRenderer.TargetType.NON_SHIP_PHYSICAL_ENTITY.ordinal());
		GL11.glPushName(entity.getId());
		RenderUtils.renderDisc(width / 2f);
		GL11.glPopName();
		GL11.glPopName();
		GL11.glScalef(1f / massScale, 1f / massScale, 1);

	}

}
