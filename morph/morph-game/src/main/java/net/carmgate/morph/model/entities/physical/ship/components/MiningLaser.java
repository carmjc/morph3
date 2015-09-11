package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.MiningLaserAnim;
import net.carmgate.morph.model.entities.physical.Asteroid;

@NeedsTarget
@ComponentKind(ComponentType.MINING_LASERS)
public class MiningLaser extends Component {

	@Inject private AnimationFactory animationFactory;
	@Inject private World world;

	private MiningLaserAnim laserAnim;

	@Override
	public boolean acceptsTarget(Object target) {
		// TODO Use distance to accept or not
		return target instanceof Asteroid;
	}

	@Override
	public
	void evalBehavior() {
		if (world.getTime() - getLastActivation() > 5000) {
			setActive(false);
		}
	}

	@PostConstruct
	public void init() {
		laserAnim = animationFactory.newInstance(AnimationType.MINING_LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTarget(getTargetHolder());
		setAnimation(laserAnim);
	}

	@Override
	public void initBehavior() {
		setActive(true);
	}
}
