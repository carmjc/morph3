package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.model.entities.physical.PhysicalEntity;

public class ShipHit extends ShipUpdated {

   private float damage;
   private PhysicalEntity aggressor;

   public void init(PhysicalEntity aggressor, PhysicalEntity ship, float damage) {
      this.aggressor = aggressor;
      super.setShip(ship);
      this.damage = damage;
   }

   public float getDamage() {
      return damage;
   }

   public PhysicalEntity getAggressor() {
      return aggressor;
   }
}
