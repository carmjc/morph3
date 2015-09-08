package net.carmgate.morph.ui.renderers.entities.ship;

import java.util.Collection;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

@Singleton
public class ShipSelectRenderer implements SelectRenderer<Ship> {

	@Inject private Logger LOGGER;

	@Inject	private Conf conf;
	@Inject private UIContext uiContext;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
		newRendererEventMgr.fire(new NewRendererFound(this));
	}

	@Override
	public void render(Ship ship) {
		final float massScale = ship.getMass();
		final float width = 256;

		Collection<Component> components = ship.getComponents().values();
		int propIndex = 0;
		int turretIndex = 0;
		int coreIndex = 0;
		int shipType = 0;
		float compScale = 3.292f * 2;

		if (ship == uiContext.getSelectedShip()) {

			GL11.glScalef(massScale, massScale, 0);
			GL11.glColor4f(1f, 1f, 1f, 0.6f);
			GL11.glRotatef(ship.getRotate(), 0, 0, 1);

			for (Component cmp : components) {
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
				GL11.glTranslatef(compX / compScale, compY / compScale, 0);

				// draw the component
				GL11.glColor4f(0, 1, 1, 1);
				GL11.glPushName(SelectRenderer.TargetType.COMPONENT.ordinal());
				GL11.glPushName(ship.getId());
				GL11.glPushName(cmp.getId());
				RenderUtils.renderDisc(256 / compScale);
				GL11.glPopName();
				GL11.glPopName();
				GL11.glPopName();

				GL11.glTranslatef(-compX / compScale, -compY / compScale, 0);
			}

			GL11.glRotatef(-ship.getRotate(), 0, 0, 1);
			GL11.glScalef(1f / massScale, 1f / massScale, 0);
		}

		GL11.glScalef(massScale, massScale, 1);
		GL11.glColor4f(1, 1, 1, 0.3f);
		GL11.glPushName(SelectRenderer.TargetType.SHIP.ordinal());
		GL11.glPushName(ship.getId());
		RenderUtils.renderDisc(width / 2f);
		GL11.glPopName();
		GL11.glPopName();
		GL11.glScalef(1f / massScale, 1f / massScale, 1);
	}

}
