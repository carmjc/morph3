package net.carmgate.morph.model.entities.physical.ship.components;


@Background
@ComponentKind(ComponentType.GENERATORS)
public class SimpleGenerator extends Component {

   @Override
   public float getEnergyDt() {
      return 1;
   }

   @Override
   public float getResourcesDt() {
      return -0.5f;
   }

}
