package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;

public class ShipAdded implements WorldEvent {
   private PhysicalEntity ship;

   public void setAddedShip(PhysicalEntity ship) {
      this.ship = ship;
   }

   public PhysicalEntity getShip() {
      return ship;
   }

}
