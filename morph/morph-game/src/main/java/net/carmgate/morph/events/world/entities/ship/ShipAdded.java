package net.carmgate.morph.events.world.entities.ship;

import net.carmgate.morph.events.world.WorldEvent;
import net.carmgate.morph.model.entities.PhysicalEntity;

public class ShipAdded implements WorldEvent {
   private PhysicalEntity ship;

   public void setAddedShip(PhysicalEntity ship) {
      this.ship = ship;
   }

   public PhysicalEntity getShip() {
      return ship;
   }

}
