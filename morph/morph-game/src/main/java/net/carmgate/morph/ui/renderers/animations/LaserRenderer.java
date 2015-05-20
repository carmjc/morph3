package net.carmgate.morph.ui.renderers.animations;

import java.util.Random;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import net.carmgate.morph.model.animations.LaserAnim;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;

public class LaserRenderer implements Renderer<LaserAnim> {

   private Random rand = new Random();

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void render(LaserAnim laserAnim) {
      if (laserAnim.getSource() != null && laserAnim.getTarget() != null) {
         RenderUtils.renderLine(laserAnim.getSource().getPos(), laserAnim.getTarget().getPos(), 10f, 0f, rand.nextFloat() * 5 + 3,
               new float[] { 1f, 0f, 0f, 1f }, new float[] { 0f, 0f, 0f, 0f });
      }
   }

}
