package net.carmgate.morph.ui.renderers.animations;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import net.carmgate.morph.GameMain;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.Align;

public class XpAwardedAnimationRenderer implements Renderer<XpAwardedAnimation> {

	@Inject private World world;
	@Inject private UIContext uiContext;

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
		newRendererEventMgr.fire(new NewRendererFound(this));
	}

	@Override
	public void render(XpAwardedAnimation anim, float alpha) {
		float percent = ((float) world.getTime() - anim.getCreationTime()) / anim.getDuration();
		float zoomFactor = uiContext.getViewport().getZoomFactor();

		GL11.glTranslatef(anim.getPos().x, anim.getPos().y - percent * 150, 0);
		GL11.glScalef(1 / zoomFactor * (1 + percent), 1 / zoomFactor * (1 + percent), 1);
		RenderUtils.renderText(GameMain.font, "+" + anim.getXpAmount() + " XP", 0, new Color(1, 1, 1, 1 - percent), Align.CENTER);
		GL11.glScalef(zoomFactor / (1 + percent), zoomFactor / (1 + percent), 1);
		GL11.glTranslatef(-anim.getPos().x, -anim.getPos().y + percent * 150, 0);
	}
}
