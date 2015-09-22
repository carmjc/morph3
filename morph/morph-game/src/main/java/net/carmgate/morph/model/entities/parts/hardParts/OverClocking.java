package net.carmgate.morph.model.entities.parts.hardParts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.parts.HardPart;

public class OverClocking extends HardPart<Laser> {

	// not available if loaded from database
	@Inject private Conf conf;

	private Float cooldownFactor;

	@Override
	public void computeEffectOnComponent(Laser cmp) {
		cmp.setCooldown((float) (cmp.getCooldown() * Math.pow(getCooldownFactor(), getLevel())));
	}

	public float getCooldownFactor() {
		return cooldownFactor;
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		cooldownFactor = conf.getFloatProperty(getClass().getCanonicalName() + ".cooldownFactor");
		if (cooldownFactor == null) {
			cooldownFactor = 0f;
		}
	}

}
