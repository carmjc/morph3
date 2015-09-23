package net.carmgate.morph.ui.widgets;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.ui.UIContext;

@Singleton
public class WidgetFactory {

	@Inject private Instance<Widget> widgets;
	@Inject private MEventManager eventManager;
	@Inject private UIContext uiContext;

	private int idGen = 0;

	public <U extends Widget> U newInstance(Class<U> widget) {
		final U u = widgets.select(widget).get();
		u.setId(idGen++);
		eventManager.scanAndRegister(u);
		uiContext.getWidgets().put(u.getId(), u);
		return u;
	}

}
