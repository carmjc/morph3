package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;


@Background
@ComponentKind(ComponentType.GENERATORS)
public class SimpleGenerator extends Component {

   @Inject private Conf conf;

   @Override
   public float getEnergyDt() {
      return conf.getFloatProperty("component.generators.energyDt");
   }

   @Override
   public float getResourcesDt() {
      return conf.getFloatProperty("component.generators.resourcesDt");
   }

}
