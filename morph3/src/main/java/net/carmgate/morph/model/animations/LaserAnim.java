package net.carmgate.morph.model.animations;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;


public class LaserAnim extends Animation {

   @Inject private World world;

   private Ship target;
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

   public Ship getTarget() {
      return target;
   }

   public void setTarget(Ship target) {
      this.target = target;
   }

   public void setSource(Ship source) {
      this.source = source;
   }
}