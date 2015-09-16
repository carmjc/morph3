package net.carmgate.morph.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class WidgetContainer extends Widget {

	private List<Widget> widgets = new ArrayList<>();

	public boolean add(Widget e) {
		return widgets.add(e);
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	@Override
	public void renderWidget() {
		for (Widget widget : widgets) {
			if (widget.isVisible()) {
				GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
				widget.renderWidget();
				GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);
			}
		}
	}

	@Override
	public void renderWidgetForSelect() {
		for (Widget widget : widgets) {
			if (widget.isVisible()) {
				GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
				widget.renderWidgetForSelect();
				GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);
			}
		}
	}

}
