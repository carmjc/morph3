package net.carmgate.morph.ui.widgets.generalpurpose;

import javax.inject.Inject;

import org.newdawn.slick.Color;
import org.slf4j.Logger;

import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;
import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.TextAlign;
import net.carmgate.morph.ui.widgets.Widget;

public class MessagesPanel extends Widget {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;
	@Inject private MessageManager messageManager;

	@Override
	public void renderInteractiveAreas() {
		// not interactive widget
	}

	@Override
	public void renderWidget() {
		int line = 1;

		// render message
		for (Message msg : messageManager.getMessages()) {
			renderUtils.renderText(RenderingManager.font, 2, 2, msg.getStr(), line++, Color.white, TextAlign.LEFT);
		}

	}
}
