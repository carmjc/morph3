package net.carmgate.morph.ui.widgets.components;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.ui.renderers.entities.ship.ComponentRenderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;

public class ComponentWidget extends Widget {

	@Inject private Logger LOGGER;
	@Inject private ComponentRenderer componentRenderer;
	@Inject private RenderUtils renderUtils;

	private Component cmp;

	public Component getCmp() {
		return cmp;
	}

	@Override
	public float getHeight() {
		return 25;
	}

	@Override
	public float getWidth() {
		return 25;
	}

	@Override
	public void renderInteractiveAreas() {
		GL11.glTranslatef(getWidth() / 2, getHeight() / 2, 0);
		GL11.glScalef(0.5f, 0.5f, 1);
		renderUtils.renderDisc(16);
		GL11.glScalef(2, 2, 1);
		GL11.glTranslatef(-getWidth() / 2, -getHeight() / 2, 0);
	}

	@Override
	public void renderWidget() {
		GL11.glTranslatef(getWidth() / 2, getHeight() / 2, 0);
		GL11.glScalef(0.5f, 0.5f, 1);
		componentRenderer.render(cmp, 1);
		GL11.glScalef(2, 2, 1);
		GL11.glTranslatef(-getWidth() / 2, -getHeight() / 2, 0);
	}

	public void setCmp(Component cmp) {
		this.cmp = cmp;
	}

}
