package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.widgets.Widget;

public final class AbsoluteLayoutContainer extends WidgetContainer {

	@Inject private UIContext uiContext;

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		m.setIdentity();
		m.m32 = -1;
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
