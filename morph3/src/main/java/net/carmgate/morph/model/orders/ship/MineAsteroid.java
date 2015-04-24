package net.carmgate.morph.model.orders.ship;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.MiningLaser;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.events.animations.AnimationStart;
import net.carmgate.morph.model.events.entities.ship.PhysicalEntityToBeRemoved;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.OrderType;
import net.carmgate.morph.model.orders.ship.move.CloseIn;

public class MineAsteroid extends Order {

   @Inject private OrderFactory orderFactory;
   @Inject private AnimationFactory animationFactory;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private MEvent<AnimationStart> animationEventMgr;
   @Inject private MEvent<PhysicalEntityToBeRemoved> removalEventMgr;

   private static final float MAX_DISTANCE = 600;
   private static final float MASS_MINED = 0.001f;
   private Asteroid asteroid;
   private final Vector2f tmpVect = new Vector2f();

   @Override
   protected void evaluate() {
      setNextEvalTime(getNextEvalTime() + 1000);

      if (getOrderee().getMoveOrder() == null || getOrderee().getMoveOrder().getParentOrder() != this) {
         final CloseIn closeInOrder = orderFactory.newInstance(OrderType.CLOSE_IN);
         closeInOrder.setDistance(MAX_DISTANCE * 0.9f);
         closeInOrder.setTarget(asteroid);
         closeInOrder.setOrderee(getOrderee());
         getOrderee().add(closeInOrder);
      }

      // Is the target asteroid close enough ?
      tmpVect.copy(asteroid.getPos()).sub(getOrderee().getPos());
      final float distance = tmpVect.length();
      if (distance > MAX_DISTANCE) {
         return;
      }

      // Create animation
      final MiningLaser laser = animationFactory.newInstance(AnimationType.MINING_LASER);
      laser.setSource(getOrderee());
      laser.setTarget(asteroid);
      final AnimationStart animationStart = worldEventFactory.newInstance(WorldEventType.ANIMATION_START);
      animationStart.setAnimation(laser);
      animationEventMgr.fire(animationStart);

      if (MASS_MINED > asteroid.getMass()) {
         getOrderee().setMass(getOrderee().getMass() + asteroid.getMass());
         asteroid.setMass(0);
         PhysicalEntityToBeRemoved removalEvent = new PhysicalEntityToBeRemoved();
         removalEvent.setEntity(asteroid);
         removalEventMgr.fire(removalEvent);
         getOrderee().add(orderFactory.newInstance(OrderType.NO_MOVE));
         setDone(true);
      } else {
         getOrderee().setMass(getOrderee().getMass() + MASS_MINED);
         asteroid.setMass(asteroid.getMass() - MASS_MINED);
      }
   }

   public Asteroid getAsteroid() {
      return asteroid;
   }

   public void setAsteroid(Asteroid asteroid) {
      this.asteroid = asteroid;
   }

}
