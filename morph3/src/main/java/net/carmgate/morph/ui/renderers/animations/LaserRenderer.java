package net.carmgate.morph.ui.renderers.animations;

import java.util.Random;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;

public class LaserRenderer implements Renderer<Laser> {

   private Random rand = new Random();

   @Override
   public void init() {
   }

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void render(Laser laser) {
      RenderUtils.renderLine(laser.getSource().getPos(), laser.getTarget().getPos(), 1f, rand.nextFloat() * 5 + 3,
            new float[] { 1f, 0f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
   }

}
