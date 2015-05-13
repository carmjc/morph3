package net.carmgate.morph.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.orders.Order;

public abstract class MoveOrder extends Order implements ForceSource {

   @Inject private Conf conf;

   private Order parentOrder;

   @Override
   protected final void evaluate() {
      Component propulsors = getOrderee().getComponents().get(ComponentType.PROPULSORS);
      if (this instanceof NoMoveOrder) {
         propulsors.setEnergyDt(0);
         return;
      }

      evaluateMove();

      // Consume energy and resources amount linked to force generated and the kind of propulsor (propulsors are of varying efficiency)
      // TODO This is only a basic implementation
      propulsors.setActive(false);
      if (!(this instanceof NoMoveOrder)) {
         float forceMag = getForce().length();
         propulsors.setEnergyDt(-forceMag * conf.getFloatProperty("component.propulsors.energyConsumptionFactor"));
         if (forceMag > 0) {
            propulsors.setActive(true);
         }

         // set orientation
         // TODO This is a very basic orientating method
         if (getForce() != null && getForce().length() != 0) {
            float angle = (float) (getForce().angleWith(Vector2f.J) / Math.PI * 180);
            getOrderee().setRotate(angle);
         }
      }
   }

   protected abstract void evaluateMove();

   public Order getParentOrder() {
      return parentOrder;
   }

   public void setParentOrder(Order parentOrder) {
      this.parentOrder = parentOrder;
   }


}
