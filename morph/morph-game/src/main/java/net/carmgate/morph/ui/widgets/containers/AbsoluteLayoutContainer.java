package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;

public final class AbsoluteLayoutContainer extends WidgetContainer {

	@Inject private RenderUtils renderUtils;
	@Inject private UIContext uiContext;

	@Override
	public void renderInteractiveAreas() {
		// GL11.glPopName();
		// GL11.glPopName();
		// for (Widget widget : getWidgets()) {
		// if (widget.isVisible()) {
		// GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(widget.getId());
		//
		// widget.renderInteractiveAreas();
		// GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);
		//
		// GL11.glPopName();
		// GL11.glPopName();
		// }
		// }
		// GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
		// GL11.glPushName(getId());
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		renderUtils.renderQuad(0, 0, getWidth(), getHeight(), getBgColor());

		m.setIdentity();
		m.m30 = -uiContext.getWindow().getWidth() / 2;
		m.m31 = -uiContext.getWindow().getHeight() / 2;
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				m.m30 += widget.getPosition()[0];
				m.m31 += widget.getPosition()[1];
				widget.renderWidget(m, vpFb);
				m.m30 -= widget.getPosition()[0];
				m.m31 -= widget.getPosition()[1];
			}
		}
	}
}
