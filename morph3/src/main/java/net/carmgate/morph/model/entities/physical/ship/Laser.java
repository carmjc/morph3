package net.carmgate.morph.model.entities.physical.ship;

public class Laser extends Component {

   public Laser(Ship ship) {
      super(ship);
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