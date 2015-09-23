package net.carmgate.morph.model.animations;

import net.carmgate.morph.ui.renderers.Renderable;

public abstract class Animation implements Renderable {

   private long duration;
   private long end;
   private long coolDown;

   public Animation() {
   }

   public long getDuration() {
      return duration;
   }

   protected void setDuration(long duration) {
      this.duration = duration;
   }

   public long getEnd() {
      return end;
   }

   public void setEnd(long animationEnd) {
      this.end = animationEnd;
   }

   public long getCoolDown() {
      return coolDown;
   }

   protected void setCoolDown(long coolDown) {
      this.coolDown = coolDown;
   }
}
