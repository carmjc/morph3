package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.Animation;

public class AnimationStart implements WorldEvent {

   private final Animation animation;

   public AnimationStart(Animation animation) {
      this.animation = animation;
   }

   public Animation getAnimation() {
      return animation;
   }

}
