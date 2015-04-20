package net.carmgate.morph.model.orders.ship;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.AnimationStart;
import net.carmgate.morph.model.events.ShipDeath;
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

   protected void onDeadShip(@MObserves ShipDeath deadShip) {
      if (deadShip.getShip() == target) {
         setDone(true);
      }
   }

   @Override
   protected void evaluate() {
      // Create animation
      final Laser laser = animationFactory.newInstance(AnimationType.LASER);
      laser.init(orderee, target);
      AnimationStart animationStart = worldEventFactory.newInstance(WorldEventType.ANIMATION_START);
      animationStart.setAnimation(laser);
      worldEventMgr.fire(animationStart);

      // Create the event
      ShipHit shipHit = worldEventFactory.newInstance(WorldEventType.SHIP_HIT);
      shipHit.init(target, 1);
      worldEventMgr.fire(shipHit);

      setNextEvalTime(getNextEvalTime() + 1000);
   }

   public void setTarget(Ship target) {
      this.target = target;
   }

}
