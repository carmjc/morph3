package net.carmgate.morph.model.animations.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.Asteroid;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;

public class MiningLaserAnim extends Animation {

   @Inject private World world;
   @Inject private Conf conf;

   private Holder<PhysicalEntity> targetHolder;
   private Holder<Ship> sourceHolder;

   public PhysicalEntity getSource() {
      return sourceHolder.get();
   }

   public Asteroid getTarget() {
      return (Asteroid) targetHolder.get();
   }

   @PostConstruct
   public void init() {
	   // TODO this should be fixed by using a single duration field
      setDuration(conf.getIntProperty("miningLaser.anim.duration")); //$NON-NLS-1$
      setEnd(world.getTime() + getDuration());
      setCoolDown(conf.getIntProperty("miningLaser.anim.cooldown")); //$NON-NLS-1$
   }

   public void setSourceHolder(Holder<Ship> holder) {
      sourceHolder = holder;
   }

   public void setTarget(Holder<PhysicalEntity> holder) {
      targetHolder = holder;
   }
}
