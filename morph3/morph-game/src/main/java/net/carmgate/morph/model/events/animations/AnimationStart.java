package net.carmgate.morph.model.events.animations;

import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.events.WorldEvent;

public class AnimationStart implements WorldEvent {

   private Animation animation;

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public Animation getAnimation() {
      return animation;
   }

}
