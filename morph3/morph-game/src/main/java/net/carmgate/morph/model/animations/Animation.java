package net.carmgate.morph.model.animations;

import net.carmgate.morph.ui.renderers.Renderable;

public abstract class Animation implements Renderable {

   private long animationDuration;
   private long animationEnd;

   public Animation() {
   }

   public long getAnimationDuration() {
      return animationDuration;
   }

   protected void setAnimationDuration(long animationDuration) {
      this.animationDuration = animationDuration;
   }

   public long getAnimationEnd() {
      return animationEnd;
   }

   protected void setAnimationEnd(long animationEnd) {
      this.animationEnd = animationEnd;
   }
}
