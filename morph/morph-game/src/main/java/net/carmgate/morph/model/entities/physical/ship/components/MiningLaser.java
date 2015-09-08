package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.MiningLaserAnim;

@ComponentKind(ComponentType.MINING_LASERS)
public class MiningLaser extends Component {

	@Inject private Logger LOGGER;
	@Inject private AnimationFactory animationFactory;
	@Inject private Conf conf;

	private MiningLaserAnim laserAnim;
	private float maxStoredResources;

	@Override
	public
	void evalBehavior() {
		// TODO Auto-generated method stub

	}

	@PostConstruct
	public void init() {
		laserAnim = animationFactory.newInstance(AnimationType.MINING_LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTarget(getTargetHolder());
		setAnimation(laserAnim);
		LOGGER.debug("laserAnimation added"); //$NON-NLS-1$
	}
}
