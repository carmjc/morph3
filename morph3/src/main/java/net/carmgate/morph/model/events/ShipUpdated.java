package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public abstract class ShipUpdated implements WorldEvent {

   private Ship ship;

   protected void setShip(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }
}