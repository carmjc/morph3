package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.LayoutHint;
import net.carmgate.morph.ui.widgets.Widget;

public final class ColumnLayoutWidgetContainer extends WidgetContainer {

	@Inject private RenderUtils renderUtils;

	@Override
	public void renderInteractiveAreas() {
		// Float horizontalSpacing = getLayoutHints().get(LayoutHint.HORIZONTAL_SPACING);
		// horizontalSpacing = horizontalSpacing != null ? horizontalSpacing : 0;
		//
		// GL11.glPopName();
		// GL11.glPopName();
		//
		// GL11.glTranslatef(getInsets()[3], getInsets()[0], 0);
		// float revertPosition = getInsets()[3];
		// for (Widget widget : getWidgets()) {
		// if (widget.isVisible()) {
		// GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(widget.getId());
		//
		// widget.renderInteractiveAreas();
		// GL11.glTranslatef(widget.getWidth() + horizontalSpacing, 0, 0);
		// revertPosition += widget.getWidth() + horizontalSpacing;
		//
		// GL11.glPopName();
		// GL11.glPopName();
		// }
		// }
		// GL11.glTranslatef(-revertPosition, -getInsets()[0], 0);
		//
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(getId());
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		renderUtils.renderQuad(0, 0, getWidth(), getHeight(), getBgColor());

		Float horizontalSpacing = getLayoutHints().get(LayoutHint.HORIZONTAL_SPACING);
		horizontalSpacing = horizontalSpacing != null ? horizontalSpacing : 0;

		GL11.glTranslatef(getInsets()[3], getInsets()[0], 0);
		float revertPosition = getInsets()[3];
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				widget.renderWidget(m, vpFb);
				GL11.glTranslatef(widget.getWidth() + horizontalSpacing, 0, 0);
				revertPosition += widget.getWidth() + horizontalSpacing;
			}
		}
		GL11.glTranslatef(-revertPosition, -getInsets()[0], 0);
	}
}
