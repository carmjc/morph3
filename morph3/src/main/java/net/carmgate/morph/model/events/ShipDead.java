package net.carmgate.morph.model.events;

import net.carmgate.morph.model.entities.physical.Ship;

public class ShipDead extends ShipUpdated {

   @Override
   public void setAttributes(Ship ship) {
      super.setAttributes(ship);
   }

}
