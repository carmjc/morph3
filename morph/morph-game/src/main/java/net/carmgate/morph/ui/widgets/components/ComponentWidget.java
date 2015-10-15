package net.carmgate.morph.ui.widgets.components;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.jbox2d.collision.shapes.PolygonShape;
import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.ui.renderers.entities.ship.ComponentRenderer;
import net.carmgate.morph.ui.widgets.Widget;

public class ComponentWidget extends Widget {

	@Inject private Logger LOGGER;
	@Inject private ComponentRenderer componentRenderer;

	private Matrix4f m = new Matrix4f();

	private Component cmp;

	public ComponentWidget() {
		shape = new PolygonShape();
		((PolygonShape) shape).setAsBox(getWidth() / 2000, getHeight() / 2000);
	}

	public Component getCmp() {
		return cmp;
	}

	@Override
	public float getHeight() {
		return 40;
	}

	@Override
	public float getWidth() {
		return 40;
	}

	@Override
	public void renderWidget(Matrix4f mTmp, FloatBuffer vpFb) {

		m.load(mTmp);
		m.m00 *= getWidth() / 50;
		m.m01 *= getHeight() / 50;
		m.m10 *= -getWidth() / 50;
		m.m11 *= -getHeight() / 50;
		m.m30 += getWidth() / 2;
		m.m31 += getHeight() / 2;

		componentRenderer.prepare();
		componentRenderer.render(cmp, 1, m, vpFb); // FIXME
		componentRenderer.clean();
	}

	public void setCmp(Component cmp) {
		this.cmp = cmp;
	}

}
