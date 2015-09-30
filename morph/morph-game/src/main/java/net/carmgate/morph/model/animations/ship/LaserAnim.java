package net.carmgate.morph.model.animations.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;


public class LaserAnim extends Animation {

	@Inject private World world;

	private Holder<PhysicalEntity> targetHolder;
	private Holder<Ship> sourceHolder;

	public PhysicalEntity getSource() {
		return sourceHolder.get();
	}

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
		setEnd(0);
	}

	public void setSourceHolder(Holder<Ship> sourceHolder) {
		this.sourceHolder = sourceHolder;
	}

	public void setTargetHolder(Holder<PhysicalEntity> targetHolder) {
		this.targetHolder = targetHolder;
	}

}
