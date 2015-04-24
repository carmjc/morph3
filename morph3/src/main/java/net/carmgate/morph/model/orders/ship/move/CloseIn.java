package net.carmgate.morph.model.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.OrderType;
import net.carmgate.morph.model.physics.ForceSource;

import org.slf4j.Logger;

public class CloseIn extends MoveOrder implements ForceSource {

   @Inject private MEvent<Order> orderMgr;
   @Inject private OrderFactory orderFactory;
   @Inject private Logger LOGGER;

   private PhysicalEntity target;
   private float desiredDistance;
   private final Vector2f force = new Vector2f();
   private final Vector2f tmpVect = new Vector2f();

   @Override
   protected void evaluate() {
      // LOGGER.debug("Closing in on (" + target.getPos() + ") from (" + getOrderee().getPos() + ")");

      tmpVect.copy(target.getPos()).sub(getOrderee().getPos());
      float actualDistance = tmpVect.length();

      float maxAccel = Ship.MAX_PROPULSOR_FORCE / getOrderee().getMass();
      Vector2f speedDiff = new Vector2f(getOrderee().getSpeed()).sub(target.getSpeed());
      float speedDiffLength = speedDiff.length();
      float breakingAccel = speedDiffLength * speedDiffLength / (2 * (actualDistance - desiredDistance));

      if (desiredDistance < actualDistance) {
         if (breakingAccel > 0.99f * maxAccel) {
            // LOGGER.debug("Breaking");
            // We need to break
            force.copy(getOrderee().getSpeed());
            if (force.length() > 0) {
               force.scale(-1f * Ship.MAX_PROPULSOR_FORCE / force.length());
            }
         } else {
            LOGGER.debug("Breaking Accel: " + breakingAccel + " - maxAccel: " + maxAccel + " - distance: " + actualDistance + " - speed: " + getOrderee().getSpeed().length());
            // No need to break now
            force.copy(target.getPos()).sub(getOrderee().getPos());
            LOGGER.debug("dist: " + force.length());
            // force.scale(getOrderee().getSpeed().length() * 2 / force.length());
            Vector2f tmp = new Vector2f(getOrderee().getSpeed()).scale(getOrderee().getSpeed().length() / maxAccel);
            // Vector2f tmp = new Vector2f(getOrderee().getSpeed());
            force.sub(tmp); // .add(target.getSpeed())
            LOGGER.debug("speed: " + getOrderee().getSpeed().length());
            LOGGER.debug("speediff: " + speedDiff.length());
            if (force.length() > 0) {
               force.scale(Ship.MAX_PROPULSOR_FORCE / force.length());
            }
            LOGGER.debug("force: " + force);
         }
      } else {
         // LOGGER.debug("Breaking");
         // We need to break
         force.copy(getOrderee().getSpeed());
         if (force.length() > 0) {
            force.scale(-1f * Ship.MAX_PROPULSOR_FORCE / force.length());
         }
      }
   }

   public float getDistance() {
      return desiredDistance;
   }

   @Override
   public Vector2f getForce() {
      return force;
   }

   public PhysicalEntity getTarget() {
      return target;
   }

   protected void onShipDeath(@MObserves ShipDeath shipDeath) {
      if (target == shipDeath.getShip()) {
         final NoMoveOrder noMoveOrder = orderFactory.newInstance(OrderType.NO_MOVE);
         orderMgr.fire(noMoveOrder);
      }
   }

   public void setDistance(float distance) {
      desiredDistance = distance;
   }

   public void setTarget(PhysicalEntity target) {
      this.target = target;
   }

}
