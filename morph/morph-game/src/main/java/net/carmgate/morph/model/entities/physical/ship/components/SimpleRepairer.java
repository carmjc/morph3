package net.carmgate.morph.model.entities.physical.ship.components;


@Background
@ComponentKind(ComponentType.REPAIRER)
public class SimpleRepairer extends Component {

   @Override
   public boolean isActive() {
      if (getShip().getIntegrity() < 1) {
         return true;
      }
      return false;
   }

   @Override
   public float getEnergyDt() {
      if (getShip().getIntegrity() < 1) {
         return -0.5f;
      }
      return 0;
   }

   @Override
   public float getResourcesDt() {
      if (getShip().getIntegrity() < 1) {
         return -0.5f;
      }
      return 0;
   }

   @Override
   public float getIntegrityDt() {
      if (getShip().getIntegrity() < 1) {
         return 0.01f;
      }
      return 0;
   }
}
