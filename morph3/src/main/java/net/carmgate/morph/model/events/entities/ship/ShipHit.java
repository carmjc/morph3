package net.carmgate.morph.model.events.entities.ship;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class ShipHit extends ShipUpdated {

   private float damage;

   public void init(Ship ship, float damage) {
      super.setShip(ship);
      this.damage = damage;
   }

   public float getDamage() {
      return damage;
   }
}
