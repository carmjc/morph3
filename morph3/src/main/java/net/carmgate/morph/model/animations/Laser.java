package net.carmgate.morph.model.animations;

import net.carmgate.morph.model.entities.physical.Ship;


public class Laser implements Animation {

   private Ship target;
   private Ship source;

   public void setAttributes(Ship source, Ship target) {
      this.source = source;
      this.target = target;
   }

   public Ship getSource() {
      return source;
   }

   public Ship getTarget() {
      return target;
   }
}
