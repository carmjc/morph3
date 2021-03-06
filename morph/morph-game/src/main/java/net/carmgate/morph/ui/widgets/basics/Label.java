package net.carmgate.morph.ui.widgets.basics;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;

public class Label extends Widget {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;

	private String text = "";

	@Override
	public float getHeight() {
		if (super.getHeight() == 0) {
			// FIXME
			// return RenderingManager.font.getTargetFontSize() + getInsets()[0] + getInsets()[2] + getOutsets()[0] + getOutsets()[2];
		}
		return super.getHeight();
	}

	public String getText() {
		return text;
	}

	@Override
	public float getWidth() {
		if (super.getWidth() == 0) {
			return RenderingManager.font.getWidth(text) + getInsets()[1] + getInsets()[3] + getOutsets()[1] + getOutsets()[3];
		}
		return super.getWidth();
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		renderUtils.renderQuad(getOutsets()[3],
				getOutsets()[0],
				getWidth() - getOutsets()[1],
				getHeight() - getOutsets()[2],
				getBgColor());
		GL11.glTranslatef(getInsets()[3] + getOutsets()[3], getInsets()[0] + getOutsets()[0], 0);
		// FIXME
		// renderUtils.renderText(RenderingManager.font, text, 1, Color.white, TextAlign.LEFT);
		GL11.glTranslatef(-getInsets()[3] - getOutsets()[3], -getInsets()[0] - getOutsets()[0], 0);
	}

	public void setText(String text) {
		this.text = text;
	}
}
