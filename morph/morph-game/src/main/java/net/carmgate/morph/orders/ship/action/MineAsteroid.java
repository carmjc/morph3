package net.carmgate.morph.orders.ship.action;

import javax.inject.Inject;

import net.carmgate.morph.events.entities.ship.PhysicalEntityToBeRemoved;
import net.carmgate.morph.events.mgt.MEvent;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.orders.OrderFactory;
import net.carmgate.morph.orders.OrderType;
import net.carmgate.morph.orders.ship.move.CloseIn;

import org.slf4j.Logger;

public class MineAsteroid extends ActionOrder {

   @Inject private Logger LOGGER;
   @Inject private OrderFactory orderFactory;
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
      Component miningLaser = getOrderee().getComponents().get(ComponentType.MINING_LASERS);
      if (distance > MAX_DISTANCE) {
         miningLaser.setEnergyDt(0);
         miningLaser.setResourcesDt(0);
         return;
      }

      LOGGER.debug("mining ?");
      if (MASS_MINED > asteroid.getMass()) {
         getOrderee().setMass(getOrderee().getMass() + asteroid.getMass());
         asteroid.setMass(0);
         PhysicalEntityToBeRemoved removalEvent = new PhysicalEntityToBeRemoved();
         removalEvent.setEntity(asteroid);
         removalEventMgr.fire(removalEvent);
         getOrderee().add(orderFactory.newInstance(OrderType.NO_MOVE, getOrderee()));
         setDone(true);

         miningLaser.setTarget(null);
         miningLaser.setActive(false);
      } else {
         getOrderee().setMass(getOrderee().getMass() + MASS_MINED);
         asteroid.setMass(asteroid.getMass() - MASS_MINED);

         miningLaser.setTarget(asteroid);
         miningLaser.setActive(true);
         LOGGER.debug("      Yes");
      }

      miningLaser.setEnergyDt(-0.5f);
      miningLaser.setResourcesDt(1f);
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

   @Override
   public void onRemoveOrder() {
      Component miningLaser = getOrderee().getComponents().get(ComponentType.MINING_LASERS);
      miningLaser.setActive(false);
   }

}
