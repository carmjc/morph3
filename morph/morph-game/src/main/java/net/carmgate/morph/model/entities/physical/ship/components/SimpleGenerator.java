package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;

@ComponentKind(ComponentType.GENERATORS)
public class SimpleGenerator extends Component {

	@Inject private Conf conf;
	@Inject private World world;

	@Override
	public float getEnergyDt() {
		return conf.getFloatProperty(getClass().getCanonicalName() + ".energyDt"); //$NON-NLS-1$
	}

	@Override
	public float getMaxStoredEnergy() {
		return conf.getFloatProperty(getClass().getCanonicalName() + ".maxStoredEnergy");
	}

	@Override
	public float getResourcesDt() {
		return conf.getFloatProperty(getClass().getCanonicalName() + ".resourcesDt"); //$NON-NLS-1$
	}

	@Override
	public void startBehavior() {
		getShip().setEnergy(getShip().getEnergy() + getEnergyDt());
		getShip().setResources(getShip().getResources() + getResourcesDt());

		setLastActivation(world.getTime());
	}

}
