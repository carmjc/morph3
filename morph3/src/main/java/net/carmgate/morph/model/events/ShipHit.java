package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class ShipHit extends ShipUpdated {

   private float damage;

   public void setAttributes(Ship ship, float damage) {
      super.setAttributes(ship);
      this.damage = damage;
   }

   public float getDamage() {
      return damage;
   }
}
