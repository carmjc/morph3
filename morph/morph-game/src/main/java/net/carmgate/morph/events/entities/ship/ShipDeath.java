package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.model.entities.physical.PhysicalEntity;

public class ShipDeath extends ShipUpdated {

   public void setDeadShip(PhysicalEntity ship) {
      super.setShip(ship);
   }

}
