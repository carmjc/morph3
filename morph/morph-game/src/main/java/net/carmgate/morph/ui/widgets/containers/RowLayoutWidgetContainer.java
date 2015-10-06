package net.carmgate.morph.ui.widgets.containers;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;

import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.LayoutHint;
import net.carmgate.morph.ui.widgets.Widget;

public final class RowLayoutWidgetContainer extends WidgetContainer {

	@Inject private RenderUtils renderUtils;

	@Override
	public void renderInteractiveAreas() {
		// Float vertSpacing = getLayoutHints().get(LayoutHint.VERTICAL_SPACING);
		// vertSpacing = vertSpacing != null ? vertSpacing : 0;
		//
		// GL11.glPopName();
		// GL11.glPopName();
		//
		// GL11.glTranslatef(getInsets()[3], getInsets()[0], 0);
		// float revertPosition = getInsets()[0];
		// for (Widget widget : getWidgets()) {
		// if (widget.isVisible()) {
		// GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(widget.getId());
		//
		// widget.renderInteractiveAreas();
		// GL11.glTranslatef(0, widget.getHeight() + vertSpacing, 0);
		// revertPosition += widget.getHeight() + vertSpacing;
		//
		// GL11.glPopName();
		// GL11.glPopName();
		// }
		// }
		//
		// GL11.glTranslatef(-getInsets()[3], -revertPosition, 0);
		//
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(getId());
	}

	@Override
	public void renderWidget() {
		Float vertSpacing = getLayoutHints().get(LayoutHint.VERTICAL_SPACING);
		vertSpacing = vertSpacing != null ? vertSpacing : 0;

		renderUtils.renderQuad(0, 0, getWidth() - getInsets()[1], getHeight() - getInsets()[3], getBgColor());

		GL11.glTranslatef(getInsets()[3], getInsets()[0], 0);
		float revertPosition = getInsets()[0];
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				widget.renderWidget();
				GL11.glTranslatef(0, widget.getHeight() + vertSpacing, 0);
				revertPosition += widget.getHeight() + vertSpacing;
			}
		}

		GL11.glTranslatef(-getInsets()[3], -revertPosition, 0);
	}
}
