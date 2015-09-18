package net.carmgate.morph.model.entities.physical.ship.components.hardParts;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.ship.components.HardPart;
import net.carmgate.morph.model.entities.physical.ship.components.laser.Laser;

public class OverClocking extends HardPart<Laser> {

	@Inject private Conf conf;

	@Override
	public void computeEffectOnComponent(Laser cmp) {
		// Float cooldown = conf.getFloatProperty(getComponent().getClass().getCanonicalName() + ".cooldown");
		// if (cooldown == null) {
		// cooldown = 0f;
		// }
		//
		cmp.setCooldown((float) (cmp.getCooldown() * Math.pow(getCooldownFactor(), getLevel())));
	}

	public float getCooldownFactor() {
		return 0.95f;
	}

	@Override
	public int getXpNeededForNextLevel() {
		return 0;
	}

}
