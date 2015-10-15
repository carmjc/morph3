package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.widgets.LayoutHint;
import net.carmgate.morph.ui.widgets.Widget;

public final class RowLayoutWidgetContainer extends WidgetContainer {

	@Override
	public float[] getPosition(Widget widget) {
		Float vertSpacing = getLayoutHints().get(LayoutHint.VERTICAL_SPACING);
		vertSpacing = vertSpacing != null ? vertSpacing : 0;
		float[] pos = new float[] { getInsets()[3], getInsets()[0] };

		for (Widget w : getWidgets()) {
			if (w == widget) {
				break;
			}
			pos[1] += widget.getHeight() + vertSpacing;
		}
		return pos;
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		Float vertSpacing = getLayoutHints().get(LayoutHint.VERTICAL_SPACING);
		vertSpacing = vertSpacing != null ? vertSpacing : 0;

		m.m30 += getInsets()[3];
		m.m31 += getInsets()[0];
		float revertPosition = getInsets()[0];
		for (Widget widget : getWidgets()) {
			if (widget.isVisible()) {
				widget.renderWidget(m, vpFb);
				m.m31 += widget.getHeight() + vertSpacing;
				revertPosition += widget.getHeight() + vertSpacing;
			}
		}

		m.m30 += -getInsets()[3];
		m.m31 += -revertPosition;
	}
}
