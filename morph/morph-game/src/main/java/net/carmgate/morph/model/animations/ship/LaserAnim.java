package net.carmgate.morph.model.animations.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;


public class LaserAnim extends Animation {

   @Inject private World world;

   private Holder<PhysicalEntity> targetHolder;
   private Holder<Ship> sourceHolder;

   @PostConstruct
   public void init() {
      setDuration(300);
      setEnd(world.getTime() + getDuration());
   }

   // FIXME
   protected void onShipDeath(@MObserves ShipDeath shipDeath) {
      setEnd(0);
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
