package net.carmgate.morph.model.orders.ship;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.events.animations.AnimationStart;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.events.entities.ship.ShipHit;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.OrderType;
import net.carmgate.morph.model.orders.ship.move.CloseIn;

import org.slf4j.Logger;

public class Attack extends Order {

   private static final float MAX_DISTANCE = 200;

   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private AnimationFactory animationFactory;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private OrderFactory orderFactory;
   @Inject private Logger LOGGER;

   private Ship target;
   private final Vector2f tmpVect = new Vector2f();

   @Override
   protected void evaluate() {
      setNextEvalTime(getNextEvalTime() + 1000);

      if (getOrderee().getMoveOrder() == null || getOrderee().getMoveOrder().getParentOrder() != this) {
         final CloseIn closeInOrder = orderFactory.newInstance(OrderType.CLOSE_IN);
         closeInOrder.setDistance(MAX_DISTANCE * 0.5f);
         closeInOrder.setTarget(target);
         closeInOrder.setOrderee(getOrderee());
         getOrderee().add(closeInOrder);
      }

      // Is the target ship close enough ?
      tmpVect.copy(target.getPos()).sub(getOrderee().getPos());
      final float distance = tmpVect.length();
      if (distance > MAX_DISTANCE) {
         return;
      }

      // Create animation
      final Laser laser = animationFactory.newInstance(AnimationType.LASER);
      laser.setSource(getOrderee());
      laser.setTarget(target);
      final AnimationStart animationStart = worldEventFactory.newInstance(WorldEventType.ANIMATION_START);
      animationStart.setAnimation(laser);
      worldEventMgr.fire(animationStart);

      // Create the event
      final ShipHit shipHit = worldEventFactory.newInstance(WorldEventType.SHIP_HIT);
      shipHit.init(target, 1);
      worldEventMgr.fire(shipHit);
   }

   protected void onDeadShip(@MObserves ShipDeath deadShip) {
      if (deadShip.getShip() == target) {
         setDone(true);
      }
   }

   public void setTarget(Ship target) {
      this.target = target;
   }

}
