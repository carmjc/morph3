package net.carmgate.morph.model.events.entities.ship;

import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.WorldEvent;

public abstract class ShipUpdated implements WorldEvent {

   private Ship ship;

   protected void setShip(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }
}
