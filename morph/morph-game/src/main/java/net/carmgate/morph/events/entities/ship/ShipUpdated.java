package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;

public abstract class ShipUpdated implements WorldEvent {

   private PhysicalEntity ship;

   protected void setShip(PhysicalEntity ship) {
      this.ship = ship;
   }

   public PhysicalEntity getShip() {
      return ship;
   }
}
