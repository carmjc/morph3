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

	@PostConstruct
	public void init() {
		laserAnim = animationFactory.newInstance(AnimationType.MINING_LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTarget(getTargetHolder());
		setAnimation(laserAnim);
		LOGGER.debug("laserAnimation added"); //$NON-NLS-1$

		maxStoredResources = conf.getFloatProperty("component.miningLaser.maxStoredResources");
	}

	@Override
	public float getEnergyDt() {
		return conf.getFloatProperty("component.miningLaser.energyDt") * getShip().getComponentsComposition().get(ComponentType.MINING_LASERS); //$NON-NLS-1$
	}

	@Override
	public float getResourcesDt() {
		return conf.getFloatProperty("component.miningLaser.resourcesDt") * getShip().getComponentsComposition().get(ComponentType.MINING_LASERS); //$NON-NLS-1$
	}

	@Override
	public float getMaxStoredResources() {
		return maxStoredResources * getShip().getComponentsComposition().get(ComponentType.MINING_LASERS);
	}

	@Override
	public
	void evalBehavior() {
		// TODO Auto-generated method stub

	}
}
