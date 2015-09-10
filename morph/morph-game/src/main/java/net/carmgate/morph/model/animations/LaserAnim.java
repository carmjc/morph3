package net.carmgate.morph.model.animations;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;


public class LaserAnim extends Animation {

   @Inject private World world;

   private Holder<PhysicalEntity> targetHolder;
   private Holder<Ship> sourceHolder;

   @PostConstruct
   public void init() {
      setAnimationDuration(300);
      setAnimationEnd(world.getTime() + getAnimationDuration());
   }

   // FIXME
   protected void onShipDeath(@MObserves ShipDeath shipDeath) {
      setAnimationEnd(0);
   }

   public PhysicalEntity getSource() {
      return sourceHolder.get();
   }

   public PhysicalEntity getTarget() {
      return (PhysicalEntity) targetHolder.get();
   }

   public void setTargetHolder(Holder<PhysicalEntity> targetHolder) {
      this.targetHolder = targetHolder;
   }

   public void setSourceHolder(Holder<Ship> sourceHolder) {
      this.sourceHolder = sourceHolder;
   }

}
