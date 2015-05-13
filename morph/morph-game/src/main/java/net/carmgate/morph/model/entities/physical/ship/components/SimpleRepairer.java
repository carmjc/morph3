package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;


@Background
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
         return conf.getFloatProperty("component.repairer.energyDt"); //$NON-NLS-1$
      }
      return 0;
   }

   @Override
   public float getResourcesDt() {
      if (getShip().getIntegrity() < 1) {
         return conf.getFloatProperty("component.repairer.resourcesDt"); //$NON-NLS-1$
      }
      return 0;
   }

   @Override
   public float getIntegrityDt() {
      if (getShip().getIntegrity() < 1) {
         return conf.getFloatProperty("component.repairer.integrityDt"); //$NON-NLS-1$
      }
      return 0;
   }
}
