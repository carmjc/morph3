package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;


@AlwaysActive
@ComponentKind(ComponentType.REPAIRER)
public class SimpleRepairer extends Component {

   @Inject private Conf conf;

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
         return conf.getFloatProperty("component.repairer.energyDt") * getShip().getComponentsComposition().get(ComponentType.REPAIRER); //$NON-NLS-1$
      }
      return 0;
   }

   @Override
   public float getResourcesDt() {
      if (getShip().getIntegrity() < 1) {
         return conf.getFloatProperty("component.repairer.resourcesDt") * getShip().getComponentsComposition().get(ComponentType.REPAIRER); //$NON-NLS-1$
      }
      return 0;
   }

   @Override
   public float getIntegrityDt() {
      if (getShip().getIntegrity() < 1) {
         return conf.getFloatProperty("component.repairer.integrityDt") * getShip().getComponentsComposition().get(ComponentType.REPAIRER); //$NON-NLS-1$
      }
      return 0;
   }
}
