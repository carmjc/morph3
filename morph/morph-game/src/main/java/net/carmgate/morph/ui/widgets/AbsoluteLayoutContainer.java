package net.carmgate.morph.ui.widgets;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;

import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

public final class AbsoluteLayoutContainer extends WidgetContainer {

	@Inject private RenderUtils renderUtils;

	@Override
	public void renderInteractiveAreas() {
		GL11.glPopName();
		GL11.glPopName();
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
				GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
				GL11.glPushName(widget.getId());

				widget.renderInteractiveAreas();
				GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);

				GL11.glPopName();
				GL11.glPopName();
			}
		}
		GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		GL11.glPushName(getId());
	}

	@Override
	public void renderWidget() {
		renderUtils.renderQuad(0, 0, getWidth(), getHeight(), getBgColor());

		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
				widget.renderWidget();
				GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);
			}
		}
	}
}
