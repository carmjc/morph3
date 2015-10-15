package net.carmgate.morph.ui.widgets.containers;

import java.util.ArrayList;
import java.util.List;

import net.carmgate.morph.ui.widgets.Widget;

public abstract class WidgetContainer extends Widget {

	private List<Widget> widgets = new ArrayList<>();

	public boolean add(Widget widget) {
		widget.setParent(this);
		return widgets.add(widget);
	}

	public abstract float[] getPosition(Widget widget);

	public List<Widget> getWidgets() {
		return widgets;
	}

}
