package net.carmgate.morph.model.animations.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.ComponentAnimation;
import net.carmgate.morph.model.entities.PhysicalEntity;


public class LaserAnim extends ComponentAnimation {

	@Inject private MWorld world;

	private Holder<PhysicalEntity> targetHolder;
	public PhysicalEntity getTarget() {
		return targetHolder.get();
	}

	@PostConstruct
	public void init() {
		setDuration(Integer.MAX_VALUE);
		setEnd(world.getTime() + getDuration());
	}

	// FIXME
	protected void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (shipDeath.getShip() == targetHolder.get()) {
			setEnd(0);
		}
	}

	public void setTargetHolder(Holder<PhysicalEntity> targetHolder) {
		this.targetHolder = targetHolder;
	}

}
