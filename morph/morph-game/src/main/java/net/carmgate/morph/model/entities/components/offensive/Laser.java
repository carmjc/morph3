package net.carmgate.morph.model.entities.components.offensive;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.entities.parts.PartFactory;
import net.carmgate.morph.model.entities.parts.hardParts.OverClocking;
import net.carmgate.morph.model.entities.ship.Ship;


@NeedsTarget
@ComponentKind(ComponentType.LASERS)
public class Laser extends Component {

	@Inject private AnimationFactory animationFactory;
	@Inject private World world;
	@Inject private PartFactory partFactory;

	private LaserAnim laserAnim;
	private OverClocking overClocking;

	@Override
	public
	void evalBehavior() {
		if (world.getTime() - getLastActivation() > getCooldown() * 1000) {
			setActive(false);

			// Remove health to target
			Ship targetShip = (Ship) getTarget();
			targetShip.setIntegrity(
					targetShip.getIntegrity() - getDamage() / targetShip.getDurability());
		}
	}

	@Override
	public void initBehavior() {
		if (getTarget() instanceof Ship) {
			setActive(true);
		}
	}

	// @Override
	// public float getCooldown() {
	// return super.getCooldown() * overClocking.getCooldownFactor();
	// }
	//
	@PostConstruct
	private void initSpecific() {
		laserAnim = animationFactory.newInstance(AnimationType.LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTargetHolder(getTargetHolder());
		setAnimation(laserAnim);

		overClocking = partFactory.newInstance(OverClocking.class);
		addPart(overClocking);
	}

	public void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (getTarget() == shipDeath.getShip()) {
			setTarget(null);
		}
	}

}
