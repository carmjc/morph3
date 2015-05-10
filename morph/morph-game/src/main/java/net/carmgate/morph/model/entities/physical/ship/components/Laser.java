package net.carmgate.morph.model.entities.physical.ship.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.LaserAnim;


@ComponentKind(ComponentType.LASERS)
public class Laser extends Component {

   @Inject private AnimationFactory animationFactory;

   private LaserAnim laserAnim;;

   @PostConstruct
   private void init() {
      laserAnim = animationFactory.newInstance(AnimationType.LASER);
      laserAnim.setSourceHolder(getShipHolder());
      laserAnim.setTargetHolder(getTargetHolder());
      setAnimation(laserAnim);

      setEnergyDt(-0.5f);
      setResourcesDt(-0.5f);
   }

}
