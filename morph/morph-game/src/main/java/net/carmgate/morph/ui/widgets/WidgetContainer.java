package net.carmgate.morph.ui.widgets;

import java.util.ArrayList;
import java.util.List;

public abstract class WidgetContainer extends Widget {

	private List<Widget> widgets = new ArrayList<>();

	public boolean add(Widget widget) {
		widget.setParent(this);
		return widgets.add(widget);
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

}
