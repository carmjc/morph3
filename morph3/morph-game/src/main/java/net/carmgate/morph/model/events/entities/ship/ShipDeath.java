package net.carmgate.morph.model.events.entities.ship;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class ShipDeath extends ShipUpdated {

   public void setDeadShip(Ship ship) {
      super.setShip(ship);
   }

}
