package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.LaserAnim;
import net.carmgate.morph.model.entities.physical.ship.Ship;


@NeedsTarget
@ComponentKind(ComponentType.LASERS)
public class Laser extends Component {

	@Inject private AnimationFactory animationFactory;
	@Inject private World world;
	@Inject private Conf conf;

	private LaserAnim laserAnim;;

	@Override
	public
	void evalBehavior() {
		if (world.getTime() - getLastActivation() > 5000) {
			setActive(false);

			// Remove health to target
			Ship targetShip = (Ship) getTarget();
			targetShip.setIntegrity(
					targetShip.getIntegrity() - conf.getFloatProperty(getClass().getCanonicalName() + ".target.damage") / targetShip.getDurability());
		}
	}

	@PostConstruct
	private void init() {
		laserAnim = animationFactory.newInstance(AnimationType.LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTargetHolder(getTargetHolder());
		setAnimation(laserAnim);
	}

	@Override
	public void initBehavior() {
		if (getTarget() instanceof Ship) {
			setActive(true);
		}
	}

	public void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (getTarget() == shipDeath.getShip()) {
			setTarget(null);
		}
	}
}
