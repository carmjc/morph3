package net.carmgate.morph.ui.widgets.containers;

import java.nio.FloatBuffer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetFactory;
import net.carmgate.morph.ui.widgets.generalpurpose.MessagesPanel;
import net.carmgate.morph.ui.widgets.radar.RadarWidget;
import net.carmgate.morph.ui.widgets.shipeditor.ShipEditorPanel;

public final class RootContainer extends WidgetContainer {

	@Inject private UIContext uiContext;
	@Inject private WidgetFactory widgetFactory;
	private ColumnLayoutWidgetContainer cmpBarWidget;

	public ColumnLayoutWidgetContainer getCmpBarWidget() {
		return cmpBarWidget;
	}

	@Override
	public float[] getPosition(Widget widget) {
		for (Widget w : getWidgets()) {
			if (w == widget) {
				return w.getPosition();
			}
		}
		return null;
	}

	@PostConstruct
	public void postConstruct() {
		MessagesPanel messagesWidget = widgetFactory.newInstance(MessagesPanel.class);
		messagesWidget.setPosition(new float[] { 0, uiContext.getWindow().getHeight(), 0 });
		add(messagesWidget);

		ShipEditorPanel shipEditorPanel = widgetFactory.newInstance(ShipEditorPanel.class);
		shipEditorPanel.setPosition(new float[] { uiContext.getWindow().getWidth() / 2, 0, 0 });
		// uiContext.getWidgetRoot().add(shipEditorPanel);

		RadarWidget radarWidget = widgetFactory.newInstance(RadarWidget.class);
		radarWidget.setWidth(150);
		radarWidget.setHeight(150);
		radarWidget.setPosition(new float[] { uiContext.getWindow().getWidth() - radarWidget.getWidth() - 10,
				radarWidget.getHeight() + 10 });
		add(radarWidget);

		cmpBarWidget = widgetFactory.newInstance(ColumnLayoutWidgetContainer.class);
		cmpBarWidget.setWidth(500);
		cmpBarWidget.setHeight(32);
		cmpBarWidget.setPosition(new float[] { 10, 10 });
		add(cmpBarWidget);

	}

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
