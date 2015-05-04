package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public class ShipAdded implements WorldEvent {
   private Ship ship;

   public void setAddedShip(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }

}
