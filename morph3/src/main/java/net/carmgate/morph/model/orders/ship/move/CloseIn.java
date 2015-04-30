package net.carmgate.morph.model.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.OrderType;

import org.slf4j.Logger;

public class CloseIn extends MoveOrder {

   private static final float EPSILON = 0.05f;

   @Inject private OrderFactory orderFactory;
   @Inject private Logger LOGGER;

   private PhysicalEntity target;
   private float desiredDistance;
   private final Vector2f force = new Vector2f();
   private final Vector2f tmpVect = new Vector2f();

   @Override
   protected void evaluateMove() {
      // LOGGER.debug("Closing in on (" + target.getPos() + ") from (" + getOrderee().getPos() + ")");

      // target offset
      tmpVect.copy(target.getPos()).sub(getOrderee().getPos());
      float actualDistance = tmpVect.length() - getDistance();

      if (actualDistance < EPSILON && getOrderee().getSpeed().lengthSquared() < EPSILON) {
         if (!getOrderee().isForceStop()) {
            getOrderee().setForceStop(true);
            getForce().copy(Vector2f.NULL);
         }
         return;
      }

      float maxAccel = Ship.MAX_PROPULSOR_FORCE / getOrderee().getMass();

      if (actualDistance < 0) {
         force.copy(getOrderee().getSpeed()).scale(-1);
         // getOrderee().debug1.copy(Vector2f.NULL);
         // getOrderee().debug2.copy(Vector2f.NULL);
         // getOrderee().debug3.copy(Vector2f.NULL);
         // getOrderee().debug4.copy(Vector2f.NULL);
      } else {
         // LOGGER.debug("dist: " + actualDistance);
         force.copy(target.getPos()).sub(getOrderee().getPos());
         // getOrderee().debug1.copy(force);
         // LOGGER.debug("toTarget: " + force.length());
         tmpVect.copy(getOrderee().getSpeed()).scale(getOrderee().getSpeed().length() / maxAccel);
         // getOrderee().debug2.copy(tmpVect);
         // LOGGER.debug("adjusted speed: " + tmpVect.length());
         force.sub(tmpVect); // .add(target.getSpeed())
         // getOrderee().debug3.copy(force);
         // LOGGER.debug("desiredSpeed: " + force.length());
         if (getOrderee().getSpeed().lengthSquared() > 0) {
            force.scale(getOrderee().getSpeed().lengthSquared() / (2 * actualDistance + getOrderee().getSpeed().length()));
         }
         // getOrderee().debug4.copy(force);
      }
      // LOGGER.debug(force.length() + " - " + Ship.MAX_PROPULSOR_FORCE);
      float length = force.length();
      if (length > Ship.MAX_PROPULSOR_FORCE) {
         // LOGGER.debug("scaling ... ");
         force.scale(Ship.MAX_PROPULSOR_FORCE / length);
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
         final NoMoveOrder noMoveOrder = orderFactory.newInstance(OrderType.NO_MOVE, getOrderee());
         getOrderee().add(noMoveOrder);
      }
   }

   public void setDistance(float distance) {
      desiredDistance = distance;
   }

   public void setTarget(PhysicalEntity target) {
      this.target = target;
   }

   @Override
   public ComponentType[] getComponentTypes() {
      return new ComponentType[] { ComponentType.PROPULSORS };
   }

   @Override
   public int getCriticity() {
      return (int) (tmpVect.copy(target.getPos()).sub(getOrderee().getPos()).length() - getDistance());
   }

}
