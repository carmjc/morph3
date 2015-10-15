package net.carmgate.morph.ui.widgets.shipeditor;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipSelected;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.parts.HardPart;
import net.carmgate.morph.model.entities.parts.Part;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.UIContext.Context;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.LayoutHint;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetFactory;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;
import net.carmgate.morph.ui.widgets.basics.Button;
import net.carmgate.morph.ui.widgets.basics.Label;
import net.carmgate.morph.ui.widgets.containers.ColumnLayoutWidgetContainer;
import net.carmgate.morph.ui.widgets.containers.RowLayoutWidgetContainer;
import net.carmgate.morph.ui.widgets.containers.WidgetContainer;

public class ShipEditorPanel extends WidgetContainer implements WidgetMouseListener {

	@Inject private Logger LOGGER;
	@Inject private UIContext uiContext;
	@Inject private RenderUtils renderUtils;
	@Inject private WidgetFactory widgetFactory;
	@Inject private ComponentManager componentManager;

	private ColumnLayoutWidgetContainer rootContainer;
	private RowLayoutWidgetContainer cmpListPanel;
	private RowLayoutWidgetContainer softHardListPanel;
	private Map<Part, Label> partLabels = new HashMap<>();
	private Map<Part, Button> partButtons = new HashMap<>();

	@PostConstruct
	private void buildPanel() {
		setWidth(uiContext.getWindow().getWidth() / 2);
		setHeight(uiContext.getWindow().getHeight());
		setBgColor(new float[] { 0.5f, 0.5f, 0.5f, 0.5f });

		rootContainer = widgetFactory.newInstance(ColumnLayoutWidgetContainer.class);
		rootContainer.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
		rootContainer.setParent(this);

		cmpListPanel = widgetFactory.newInstance(RowLayoutWidgetContainer.class);
		cmpListPanel.setWidth(150);
		cmpListPanel.setBgColor(new float[] { 1f, 0, 0, 0.1f });
		cmpListPanel.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
		rootContainer.add(cmpListPanel);

		softHardListPanel = widgetFactory.newInstance(RowLayoutWidgetContainer.class);
		softHardListPanel.setWidth(getWidth() - 150);
		softHardListPanel.setBgColor(new float[] { 0, 1f, 0, 0.1f });
		softHardListPanel.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
		rootContainer.add(softHardListPanel);
	}

	@Override
	public float[] getPosition(Widget widget) {
		// FIXME Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		return uiContext.getContext() == Context.SHIP_EDITOR
				&& uiContext.getSelectedShip() != null;
	}

	private void onCmpSelected(Component cmp) {
		softHardListPanel.getWidgets().clear();

		RowLayoutWidgetContainer hardListPanel = widgetFactory.newInstance(RowLayoutWidgetContainer.class);
		hardListPanel.getLayoutHints().put(LayoutHint.FILL_HORIZONTAL, null);
		hardListPanel.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
		hardListPanel.getLayoutHints().put(LayoutHint.VERTICAL_SPACING, 2f);
		hardListPanel.setInsets(new float[] { 5f, 5f, 5f, 5f });
		softHardListPanel.add(hardListPanel);

		Label label = widgetFactory.newInstance(Label.class);
		label.setText("Hardware upgrades");
		label.setOutsets(new float[] { 0, 0, 2, 0 });
		hardListPanel.add(label);

		ColumnLayoutWidgetContainer labelButtonPanel = widgetFactory.newInstance(ColumnLayoutWidgetContainer.class);
		labelButtonPanel.getLayoutHints().put(LayoutHint.FILL_HORIZONTAL, null);
		labelButtonPanel.getLayoutHints().put(LayoutHint.HORIZONTAL_SPACING, 5f);
		// FIXME
		// labelButtonPanel.setHeight(RenderingManager.font.getTargetFontSize() + 5);
		labelButtonPanel.setInsets(new float[] { 1f, 1f, 1f, 1f });
		hardListPanel.add(labelButtonPanel);

		for (final HardPart hardPart : cmp.getHardParts()) {

			label = widgetFactory.newInstance(Label.class);
			partLabels.put(hardPart, label);
			label.setText(hardPart.getClass().getSimpleName() + ": level " + hardPart.getLevel());
			label.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
			label.setInsets(new float[] { 2f, 0f, 2f, 0f });
			labelButtonPanel.add(label);

			Button button = widgetFactory.newInstance(Button.class);
			partButtons.put(hardPart, button);
			button.setText("^ " + hardPart.getXpNeededForNextLevel() + " XP");
			button.setBgColor(new float[] { 0, 0, 0, 0.5f });
			button.setInsets(new float[] { 2f, 4f, 2f, 4f });
			button.getLayoutHints().put(LayoutHint.FILL_VERTICAL, null);
			button.addWidgetMouseListener(new WidgetMouseListener() {
				@Override
				public void onClick() {
					upgradeHardPart(hardPart);
				}
			});
			labelButtonPanel.add(button);

		}

		//		RowLayoutWidgetContainer softListPanel = widgetFactory.newInstance(RowLayoutWidgetContainer.class);

	}

	@SuppressWarnings("unused")
	private void onShipSelected(@MObserves ShipSelected shipSelected) {

		cmpListPanel.getWidgets().clear();

		for (final Component cmp : shipSelected.getShip().getComponents().values()) {
			Button button = widgetFactory.newInstance(Button.class);
			button.setBgColor(new float[] { 1, 1, 1, 0.1f });
			button.setText(cmp.getClass().getSimpleName());
			button.setInsets(new float[] { 5f, 5f, 5f, 5f });
			button.setOutsets(new float[] { 0.5f, 1f, 0.5f, 1f });
			button.getLayoutHints().put(LayoutHint.FILL_HORIZONTAL, null);
			button.addWidgetMouseListener(new WidgetMouseListener() {
				@Override
				public void onClick() {
					onCmpSelected(cmp);
				}
			});
			cmpListPanel.add(button);
		}

	}

	//	@Override
	//	public void renderInteractiveAreas() {
	//		rootContainer.renderInteractiveAreas();
	//	}
	//
	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		renderUtils.renderQuad(0, 0, getWidth(), getHeight(), getBgColor());

		rootContainer.renderWidget(m, vpFb);
	}

	private <C extends Component> void upgradeHardPart(Part<C> part) {
		Ship ship = part.getComponent().getShip();
		if (ship.getXp() >= part.getXpNeededForNextLevel()) {
			LOGGER.debug("Upgraded");
			part.setLevel(part.getLevel() + 1);
			ship.setXp(ship.getXp() - part.getXpNeededForNextLevel());
			componentManager.init(part.getComponent());

			partLabels.get(part).setText(part.getClass().getSimpleName() + ": level " + part.getLevel());
			partButtons.get(part).setText("^ " + part.getXpNeededForNextLevel() + " XP");
		}
	}

}
