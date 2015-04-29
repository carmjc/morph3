package net.carmgate.morph.model.entities.physical.ship;

@Background
public class SimpleGenerator extends Component {

   public SimpleGenerator(Ship ship) {
      super(ship);
      // TODO Auto-generated constructor stub
   }

   @Override
   public float getEnergyDt() {
      return 1;
   }

   @Override
   public float getResourcesDt() {
      return -0.5f;
   }

}