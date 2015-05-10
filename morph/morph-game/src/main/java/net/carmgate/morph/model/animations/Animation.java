package net.carmgate.morph.model.animations;

import net.carmgate.morph.ui.renderers.Renderable;

public abstract class Animation implements Renderable {

   private long animationDuration;
   private long animationEnd;
   private long animationCoolDown;

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

   public void setAnimationEnd(long animationEnd) {
      this.animationEnd = animationEnd;
   }

   public long getAnimationCoolDown() {
      return animationCoolDown;
   }

   protected void setAnimationCoolDown(long animationCoolDown) {
      this.animationCoolDown = animationCoolDown;
   }
}
