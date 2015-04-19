package net.carmgate.morph.model.orders.ship;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.AnimationStart;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.orders.Order;

public class Attack extends Order {

   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private AnimationFactory animationFactory;
   @Inject private WorldEventFactory worldEventFactory;

   private Ship target;

   @Override
   protected void evaluate() {
      // Create animation
      final Laser laser = animationFactory.newInstance(AnimationType.LASER);
      laser.setAttributes(orderee, target);
      AnimationStart animationStart = worldEventFactory.newInstance(WorldEventType.ANIMATION_START);
      animationStart.setAttributes(laser);
      worldEventMgr.fire(animationStart);

      // Create the event
      ShipHit shipHit = worldEventFactory.newInstance(WorldEventType.SHIP_HIT);
      shipHit.setAttributes(target, 1);
      worldEventMgr.fire(shipHit);

      setNextEvalTime(getNextEvalTime() + 1000);
   }

   public void setAttributes(Ship target) {
      this.target = target;

   }

}
