package net.carmgate.morph.orders.ship.action;

import javax.inject.Inject;

import net.carmgate.morph.events.WorldEventFactory;
import net.carmgate.morph.events.WorldEventType;
import net.carmgate.morph.events.animations.AnimationStart;
import net.carmgate.morph.events.entities.ship.PhysicalEntityToBeRemoved;
import net.carmgate.morph.events.mgt.MEvent;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.MiningLaserAnim;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.orders.OrderFactory;
import net.carmgate.morph.orders.OrderType;
import net.carmgate.morph.orders.ship.move.CloseIn;

public class MineAsteroid extends ActionOrder {

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
         final CloseIn closeInOrder = orderFactory.newInstance(OrderType.CLOSE_IN, getOrderee());
         closeInOrder.setDistance(MAX_DISTANCE * 0.9f);
         closeInOrder.setTarget(asteroid);
         closeInOrder.setParentOrder(this);
         getOrderee().add(closeInOrder);
      }

      // Is the target asteroid close enough ?
      tmpVect.copy(asteroid.getPos()).sub(getOrderee().getPos());
      final float distance = tmpVect.length();
      if (distance > MAX_DISTANCE) {
         getOrderee().getComponents().get(ComponentType.MINING_LASERS).setEnergyDt(0);
         getOrderee().getComponents().get(ComponentType.MINING_LASERS).setResourcesDt(0);
         return;
      }

      // Create animation
      final MiningLaserAnim laser = animationFactory.newInstance(AnimationType.MINING_LASER);
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
         getOrderee().add(orderFactory.newInstance(OrderType.NO_MOVE, getOrderee()));
         setDone(true);
      } else {
         getOrderee().setMass(getOrderee().getMass() + MASS_MINED);
         asteroid.setMass(asteroid.getMass() - MASS_MINED);
      }

      getOrderee().getComponents().get(ComponentType.MINING_LASERS).setEnergyDt(-0.5f);
      getOrderee().getComponents().get(ComponentType.MINING_LASERS).setResourcesDt(1f);
   }

   public Asteroid getAsteroid() {
      return asteroid;
   }

   public void setAsteroid(Asteroid asteroid) {
      this.asteroid = asteroid;
   }

   @Override
   public ComponentType[] getComponentTypes() {
      return new ComponentType[] { ComponentType.MINING_LASERS };
   }

   @Override
   public int getCriticity() {
      return 40;
   }

}
