package net.carmgate.morph.model.events;

import net.carmgate.morph.model.animations.Animation;

public class AnimationStart implements WorldEvent {

   private Animation animation;

   public void setAttributes(Animation animation) {
      this.animation = animation;
   }

   public Animation getAnimation() {
      return animation;
   }

}
