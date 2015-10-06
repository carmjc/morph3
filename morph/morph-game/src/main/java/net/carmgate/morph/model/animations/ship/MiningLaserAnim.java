package net.carmgate.morph.model.animations.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.ComponentAnimation;
import net.carmgate.morph.model.entities.Asteroid;
import net.carmgate.morph.model.entities.PhysicalEntity;

public class MiningLaserAnim extends ComponentAnimation {

	@Inject private World world;
	@Inject private Conf conf;

	private Holder<PhysicalEntity> targetHolder;

	public Asteroid getTarget() {
		return (Asteroid) targetHolder.get();
	}

	@PostConstruct
	public void init() {
		// TODO this should be fixed by using a single duration field
		setDuration(conf.getIntProperty("miningLaser.anim.duration")); //$NON-NLS-1$
		setEnd(world.getTime() + getDuration());
		setCoolDown(conf.getIntProperty("miningLaser.anim.cooldown")); //$NON-NLS-1$
	}

	public void setTarget(Holder<PhysicalEntity> holder) {
		targetHolder = holder;
	}
}
