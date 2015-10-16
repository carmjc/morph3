package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.renderers.StringRenderable;
import net.carmgate.morph.ui.renderers.StringRenderer;
import net.carmgate.morph.ui.widgets.Widget;

public class ShipPropertyWidget extends Widget {

	@Inject private Logger LOGGER;
	@Inject private MessageManager messageManager;
	@Inject private StringRenderer stringRenderer;
	@Inject private MWorld world;
	@Inject private Ship pShip;

	private StringRenderable strR = new StringRenderable();
	private Matrix4f m = new Matrix4f();

	public ShipPropertyWidget() {
		strR.setFontSize(20);
		strR.getPos().copy(Vec2.NULL);
	}

	@PostConstruct
	private void postConstruct() {
		pShip = world.getPlayerShip();
	}

	@Override
	public void renderWidget(Matrix4f mTmp, FloatBuffer vpFb) {
		StringBuilder sb = new StringBuilder();
		sb.append("Health: ").append((int) pShip.getIntegrity() * 100);
		sb.append("\n").append("Energy: ").append((int) pShip.getEnergy());
		sb.append("\n").append("Resources: ").append(pShip.getResources());
		sb.append("\n").append("XP: ").append(pShip.getXp());

		strR.setStr(sb.toString());

		m.load(mTmp);
		m.m30 += 4;
		m.m31 += -4;

		stringRenderer.prepare();
		stringRenderer.render(strR, 1, vpFb, m);
		stringRenderer.clean();
	}

}
