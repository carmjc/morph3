package net.carmgate.morph.ui.renderers.animations;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.opengl.GL11;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

public class XpAwardedAnimationRenderer implements Renderer<XpAwardedAnimation> {

	@Inject private MWorld world;
	@Inject private UIContext uiContext;
	@Inject private RenderUtils renderUtils;

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(XpAwardedAnimation anim, float alpha, FloatBuffer vpFb) {
		float percent = ((float) world.getTime() - anim.getCreationTime()) / anim.getDuration();
		float zoomFactor = uiContext.getViewport().getZoomFactor();

		GL11.glTranslatef(anim.getPos().x, anim.getPos().y - percent * 150, 0);
		GL11.glScalef(1 / zoomFactor * (1 + percent), 1 / zoomFactor * (1 + percent), 1);
		// FIXME
		// renderUtils.renderText(RenderingManager.font, "+" + anim.getXpAmount() + " XP", 0, new Color(1, 1, 1, 1 - percent), TextAlign.CENTER);
		GL11.glScalef(zoomFactor / (1 + percent), zoomFactor / (1 + percent), 1);
		GL11.glTranslatef(-anim.getPos().x, -anim.getPos().y + percent * 150, 0);
	}
}
