package net.carmgate.morph.model.entities.parts.hardParts;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.parts.HardPart;

public class OverClocking extends HardPart<Laser> {

	@Inject private Conf conf;

	@Override
	public void computeEffectOnComponent(Laser cmp) {
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
