package net.carmgate.morph.model.animations;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public class MiningLaserAnim extends Animation {

   @Inject private World world;

   private Asteroid target;
   private Ship source;

   @PostConstruct
   public void init() {
      setAnimationDuration(300);
      setAnimationEnd(world.getTime() + getAnimationDuration());
   }

   protected void onShipDeath(@MObserves ShipDeath shipDeath) {
      setAnimationEnd(0);
   }

   public Ship getSource() {
      return source;
   }

   public Asteroid getTarget() {
      return target;
   }

   public void setTarget(Asteroid target) {
      this.target = target;
   }

   public void setSource(Ship source) {
      this.source = source;
   }
}
