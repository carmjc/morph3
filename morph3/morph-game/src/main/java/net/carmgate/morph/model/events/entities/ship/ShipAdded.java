package net.carmgate.morph.model.events.entities.ship;

import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.events.WorldEvent;

public class ShipAdded implements WorldEvent {
   private Ship ship;

   public void setAddedShip(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }

}
