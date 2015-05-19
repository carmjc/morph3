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

   @PostConstruct
   private void init() {
      laserAnim = animationFactory.newInstance(AnimationType.LASER);
      laserAnim.setSourceHolder(getShipHolder());
      laserAnim.setTargetHolder(getTargetHolder());
      setAnimation(laserAnim);
   }

   @Override
   public float getEnergyDt() {
      return conf.getFloatProperty("component.laser.energyDt") * getShip().getComponentsComposition().get(ComponentType.LASERS); //$NON-NLS-1$
   }

   @Override
   public float getResourcesDt() {
      return conf.getFloatProperty("component.laser.resourcesDt") * getShip().getComponentsComposition().get(ComponentType.LASERS); //$NON-NLS-1$
   }

}
