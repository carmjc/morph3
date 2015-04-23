package net.carmgate.morph.model.animations;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;


public class Laser extends Animation {

   @Inject private World world;

   private Ship target;
   private Ship source;

   public void init(Ship source, Ship target) {
      this.source = source;
      this.target = target;
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
}
