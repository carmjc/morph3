package net.carmgate.morph.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.orders.Order;
import net.carmgate.morph.orders.OrderFactory;

public abstract class MoveOrder extends Order implements ForceSource {

   @Inject private OrderFactory orderFactory;

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
         propulsors.setEnergyDt(-forceMag / 40);
         if (forceMag > 0) {
            propulsors.setActive(true);
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
