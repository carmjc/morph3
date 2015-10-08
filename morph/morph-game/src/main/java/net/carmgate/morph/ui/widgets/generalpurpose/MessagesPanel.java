package net.carmgate.morph.ui.widgets.generalpurpose;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.util.vector.Matrix4f;
import org.slf4j.Logger;

import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;
import net.carmgate.morph.ui.renderers.StringRenderable;
import net.carmgate.morph.ui.renderers.StringRenderer;
import net.carmgate.morph.ui.widgets.Widget;

public class MessagesPanel extends Widget {

	@Inject private Logger LOGGER;
	@Inject private MessageManager messageManager;
	@Inject private StringRenderer stringRenderer;

	private StringRenderable strR = new StringRenderable();

	public MessagesPanel() {
		strR.setSize(20);
		strR.getPos().copy(Vec2.NULL);
	}

	@Override
	public void renderInteractiveAreas() {
		// not interactive widget
	}

	@Override
	public void renderWidget(Matrix4f m, FloatBuffer vpFb) {
		StringBuilder sb = new StringBuilder();
		for (Message msg : messageManager.getMessages()) {
			if (sb.length() > 0) {
				sb.append(10);
			}
			sb.append(msg.getStr());
		}
		strR.setStr(sb.toString());

		m.m30 += 4;
		m.m31 += -4;

		stringRenderer.prepare();
		stringRenderer.render(strR, 1, vpFb, m);
		stringRenderer.clean();

	}
}
