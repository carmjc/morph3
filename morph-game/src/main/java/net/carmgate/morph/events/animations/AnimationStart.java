package net.carmgate.morph.events.animations;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.animations.Animation;

public class AnimationStart implements WorldEvent {

   private Animation animation;

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public Animation getAnimation() {
      return animation;
   }

}
