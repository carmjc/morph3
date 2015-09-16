package net.carmgate.morph.ui.widgets.shipeditor;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.UIContext.Context;
import net.carmgate.morph.ui.renderers.MorphFont;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.TextAlign;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;

public class ShipEditorPanel extends Widget implements WidgetMouseListener {

	@Inject private UIContext uiContext;
	@Inject private RenderUtils renderUtils;

	@Override
	public boolean isVisible() {
		return uiContext.getContext() == Context.SHIP_EDITOR
				&& uiContext.getSelectedShip() != null;
	}

	@Override
	public void onDrag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderInteractiveAreas() {
		Ship selectedShip = uiContext.getSelectedShip();
		float line = 1;
		if (selectedShip != null) {
			for (Component cmp : selectedShip.getComponents().values()) {
				MorphFont font = RenderingManager.font;
				GL11.glPushName(cmp.getId());
				GL11.glPushName(1);
				renderUtils.renderQuad(200,
						3 + (line - 1) * font.getTargetFontSize(),
						200 + font.getWidth("level 1") * font.getTargetFontSize() / font.getLineHeight(),
						3 + line * font.getTargetFontSize(),
						new float[] { 1, 1, 1, 1 });
				GL11.glPopName();
				line++;
				renderUtils.renderQuad(200,
						3 + (line - 1) * font.getTargetFontSize(),
						200 + font.getWidth("level 1") * font.getTargetFontSize() / font.getLineHeight(),
						3 + line * font.getTargetFontSize(),
						new float[] { 1, 1, 1, 1 });
				GL11.glPopName();
				line++;
				line++;
			}
		}
	}

	@Override
	public void renderWidget() {
		renderUtils.renderQuad(0, 0, uiContext.getWindow().getWidth() / 2, uiContext.getWindow().getHeight(),
				new float[] { 0.5f, 0.5f, 0.5f, 0.5f });

		Ship selectedShip = uiContext.getSelectedShip();
		float line = 1;
		for (Component cmp : selectedShip.getComponents().values()) {
			renderUtils.renderText(RenderingManager.font, 200, 3, "Efficiency: ", line, Color.white, TextAlign.RIGHT);
			renderUtils.renderText(RenderingManager.font, 200, 3, "level 1", line++, Color.white, TextAlign.LEFT);
			renderUtils.renderText(RenderingManager.font, 3, 3, cmp.getClass().getSimpleName(), line, Color.white, TextAlign.LEFT);
			renderUtils.renderText(RenderingManager.font, 200, 3, "Cooldown: ", line, Color.white, TextAlign.RIGHT);
			renderUtils.renderText(RenderingManager.font, 200, 3, "level 1", line++, Color.white, TextAlign.LEFT);
			line++;
		}
	}

	@Override
	public void renderWidgetForSelect() {
		Ship selectedShip = uiContext.getSelectedShip();
		float line = 1;
		for (Component cmp : selectedShip.getComponents().values()) {
			MorphFont font = RenderingManager.font;
			renderUtils.renderQuad(200, 3 + (line - 1) * font.getTargetFontSize(), font.getWidth("level 1"), font.getTargetFontSize(),
					new float[] { 1, 1, 1, 1 });
			renderUtils.renderText(font, 200, 3, "level 1", line++, Color.white, TextAlign.LEFT);
			renderUtils.renderText(font, 3, 3, cmp.getClass().getSimpleName(), line, Color.white, TextAlign.LEFT);
			renderUtils.renderText(font, 200, 3, "Cooldown: ", line, Color.white, TextAlign.RIGHT);
			renderUtils.renderText(font, 200, 3, "level 1", line++, Color.white, TextAlign.LEFT);
			line++;
		}
	}
}
