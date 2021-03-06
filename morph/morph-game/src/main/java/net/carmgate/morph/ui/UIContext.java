package net.carmgate.morph.ui;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.events.world.entities.ship.ShipSelected;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.containers.RootContainer;

@Singleton
public class UIContext {

	public static enum Context {
		GAME,
		SHIP_EDITOR;
	}

	@Inject private ViewPort viewport;
	@Inject private Window window;
	@Inject private MEventManager eventManager;
	@Inject private MWorld world;

	private RenderMode renderMode = RenderMode.NORMAL;
	private Ship selectedShip;
	private Widget selectedWidget;
	private RootContainer widgetRoot;
	private Map<Integer, Widget> widgets = new WeakHashMap<>();
	private Component selectedCmp;
	private Context context = Context.GAME;

	public Context getContext() {
		return context;
	}

	public RenderMode getRenderMode() {
		return renderMode;
	}

	public Component getSelectedCmp() {
		return selectedCmp;
	}

	public Ship getSelectedShip() {
		return selectedShip;
	}

	public Widget getSelectedWidget() {
		return selectedWidget;
	}

	public ViewPort getViewport() {
		return viewport;
	}

	public RootContainer getWidgetRoot() {
		return widgetRoot;
	}

	public Map<Integer, Widget> getWidgets() {
		return widgets;
	}

	public Window getWindow() {
		return window;
	}

	@PostConstruct
	private void init() {
		eventManager.scanAndRegister(this);
	}

	public void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (selectedShip == shipDeath.getShip()) {
			selectedShip = null;
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setRenderMode(RenderMode renderMode) {
		this.renderMode = renderMode;
	}

	public void setSelectedCmp(Component pickedCmp) {
		selectedCmp = pickedCmp;
	}

	public void setSelectedShip(Ship selectedShip) {
		if (selectedShip != null) {
			this.selectedShip = selectedShip;
			ShipSelected event = new ShipSelected();
			event.setShip(selectedShip);
			eventManager.addEvent(event);
		}
	}

	public void setSelectedWidget(Widget selectedWidget) {
		this.selectedWidget = selectedWidget;
	}

	public void setWidgetRoot(RootContainer widgetRoot) {
		this.widgetRoot = widgetRoot;
	}

	public void setWidgets(Map<Integer, Widget> widgets) {
		this.widgets = widgets;
	}

}
