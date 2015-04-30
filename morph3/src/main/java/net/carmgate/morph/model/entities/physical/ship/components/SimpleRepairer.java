package net.carmgate.morph.model.entities.physical.ship.components;

import net.carmgate.morph.model.entities.physical.ship.Ship;

@Background
public class SimpleRepairer extends Component {

   public SimpleRepairer(Ship ship) {
      super(ship);
      // TODO Auto-generated constructor stub
   }

   @Override
   public float getEnergyDt() {
      return -0.5f;
   }

   @Override
   public float getResourcesDt() {
      return -0.5f;
   }

}
