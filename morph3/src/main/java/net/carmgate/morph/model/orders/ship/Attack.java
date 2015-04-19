package net.carmgate.morph.model.orders.ship;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.AnimationStart;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.orders.Order;

public class Attack extends Order {

   @Inject
   private MEvent<WorldEvent> worldEventMgr;

   private Ship target;

   @Override
   protected void evaluate() {
      // Create animation
      final Laser laser = new Laser(orderee, target);
      worldEventMgr.fire(new AnimationStart(laser));

      // Create the event
      worldEventMgr.fire(new ShipHit(target, 1));

      setNextEvalTime(getNextEvalTime() + 1000);
   }

   public void setAttributes(Ship target) {
      this.target = target;

   }

}
