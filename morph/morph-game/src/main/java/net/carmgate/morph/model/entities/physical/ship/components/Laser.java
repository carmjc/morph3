package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.LaserAnim;


@ComponentKind(ComponentType.LASERS)
public class Laser extends Component {

	@Inject private AnimationFactory animationFactory;
	@Inject private Conf conf;

	private LaserAnim laserAnim;;

	@Override
	public
	void evalBehavior() {
		// TODO Auto-generated method stub

	}

	@PostConstruct
	private void init() {
		laserAnim = animationFactory.newInstance(AnimationType.LASER);
		laserAnim.setSourceHolder(getShipHolder());
		laserAnim.setTargetHolder(getTargetHolder());
		setAnimation(laserAnim);
	}

}
