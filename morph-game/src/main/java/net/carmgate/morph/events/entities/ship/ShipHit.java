package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class ShipHit extends ShipUpdated {

   private float damage;
   private Ship aggressor;

   public void init(Ship aggressor, Ship ship, float damage) {
      this.aggressor = aggressor;
      super.setShip(ship);
      this.damage = damage;
   }

   public float getDamage() {
      return damage;
   }

   public Ship getAggressor() {
      return aggressor;
   }
}
