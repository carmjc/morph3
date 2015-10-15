package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.ui.widgets.LayoutHint;
import net.carmgate.morph.ui.widgets.Widget;

public final class ColumnLayoutWidgetContainer extends WidgetContainer {

	@Inject private Logger LOGGER;

	@Override
	public float[] getPosition(Widget widget) {
		Float horizontalSpacing = getLayoutHints().get(LayoutHint.HORIZONTAL_SPACING);
		horizontalSpacing = horizontalSpacing != null ? horizontalSpacing : 0;
		float[] pos = new float[] { getInsets()[3] + getParent().getPosition(this)[0], getInsets()[0] + getParent().getPosition(this)[1] };

		for (Widget w : getWidgets()) {
			if (w == widget) {
				break;
			}
			pos[0] += widget.getWidth() + horizontalSpacing;
		}
		return pos;
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		Float horizontalSpacing = getLayoutHints().get(LayoutHint.HORIZONTAL_SPACING);
		horizontalSpacing = horizontalSpacing != null ? horizontalSpacing : 0;

		m.m30 += getInsets()[3];
		m.m31 += getInsets()[0];
		float revertPosition = getInsets()[3];
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				widget.renderWidget(m, vpFb);
				m.m30 += widget.getWidth() + horizontalSpacing;
				revertPosition += widget.getWidth() + horizontalSpacing;
			}
		}
		m.m30 += -revertPosition;
		m.m31 += -getInsets()[0];
	}
}
