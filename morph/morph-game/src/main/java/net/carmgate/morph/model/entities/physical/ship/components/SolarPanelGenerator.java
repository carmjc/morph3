package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;

@AlwaysActive
@ComponentKind(ComponentType.GENERATORS)
public class SolarPanelGenerator extends Component {

   @Inject private Conf conf;

   private float maxStoredEnergy;

   @PostConstruct
   private void init() {
      maxStoredEnergy = conf.getFloatProperty("component.generators.maxStoredEnergy");
   }

   @Override
   public float getEnergyDt() {
      return conf.getFloatProperty("component.generators.solar.energyDt") * getShip().getComponentsComposition().get(ComponentType.GENERATORS);
   }

   @Override
   public float getResourcesDt() {
      return conf.getFloatProperty("component.generators.solar.resourcesDt") * getShip().getComponentsComposition().get(ComponentType.GENERATORS); //$NON-NLS-1$
   }

   @Override
   public float getMaxStoredEnergy() {
      return maxStoredEnergy * getShip().getComponentsComposition().get(ComponentType.GENERATORS);
   }

}
