package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;

@ComponentKind(ComponentType.GENERATORS)
public class SolarPanelGenerator extends Component {

	@Inject private Conf conf;

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
		return conf.getFloatProperty("component.generators.maxStoredEnergy") * getShip().getComponentsComposition().get(ComponentType.GENERATORS);
	}

	@Override
	public
	void evalBehavior() {
		// TODO Auto-generated method stub

	}

}
