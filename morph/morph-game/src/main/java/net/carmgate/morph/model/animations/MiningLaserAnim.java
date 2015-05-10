package net.carmgate.morph.model.animations;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public class MiningLaserAnim extends Animation {

   @Inject private World world;

   private Holder<PhysicalEntity> targetHolder;
   private Holder<Ship> sourceHolder;

   @PostConstruct
   public void init() {
      setAnimationDuration(300);
      setAnimationEnd(world.getTime() + getAnimationDuration());
      setAnimationCoolDown(700);
   }

   public Ship getSource() {
      return sourceHolder.get();
   }

   public Asteroid getTarget() {
      return (Asteroid) targetHolder.get();
   }

   public void setTarget(Holder<PhysicalEntity> holder) {
      targetHolder = holder;
   }

   public void setSourceHolder(Holder<Ship> holder) {
      sourceHolder = holder;
   }
}
