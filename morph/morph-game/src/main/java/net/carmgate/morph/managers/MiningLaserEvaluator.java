package net.carmgate.morph.managers;

import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;

public class MiningLaserEvaluator extends ComponentBehavior<MiningLaser> {

	@Inject private World world;

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
