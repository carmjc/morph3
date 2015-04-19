package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class ShipAdded implements WorldEvent {
   private Ship ship;

   public void setAttributes(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }

}
