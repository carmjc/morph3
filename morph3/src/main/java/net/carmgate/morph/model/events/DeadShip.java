package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class DeadShip extends ShipUpdated {

   public void setDeadShip(Ship ship) {
      super.setShip(ship);
   }

}
