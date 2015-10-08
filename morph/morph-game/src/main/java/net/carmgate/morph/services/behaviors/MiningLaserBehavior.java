package net.carmgate.morph.services.behaviors;

import javax.inject.Inject;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;

public class MiningLaserBehavior extends ComponentBehavior<MiningLaser> {

	@Inject private MWorld world;

	@Override
	public void eval(MiningLaser cmp) {
		if (world.getTime() - cmp.getLastActivation() > 5000) {
			cmp.setActive(false);
		}
	}

	@Override
	public void init(MiningLaser cmp) {
		cmp.setActive(true);
	}

}
