package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.MiningLaserAnim;

import org.slf4j.Logger;

@ComponentKind(ComponentType.MINING_LASERS)
public class MiningLaser extends Component {

   @Inject private Logger LOGGER;
   @Inject private AnimationFactory animationFactory;

   private MiningLaserAnim laserAnim;

   @PostConstruct
   public void init() {
      laserAnim = animationFactory.newInstance(AnimationType.MINING_LASER);
      laserAnim.setSourceHolder(getShipHolder());
      laserAnim.setTarget(getTargetHolder());
      setAnimation(laserAnim);
      LOGGER.debug("laserAnimation added");
   }

}
