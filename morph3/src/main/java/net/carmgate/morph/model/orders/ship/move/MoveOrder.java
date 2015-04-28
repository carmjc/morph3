package net.carmgate.morph.model.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.model.entities.physical.ship.ComponentType;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.physics.ForceSource;

public abstract class MoveOrder extends Order implements ForceSource {

   @Inject private OrderFactory orderFactory;

   private Order parentOrder;

   @Override
   protected final void evaluate() {
      if (this instanceof NoMoveOrder) {
         getOrderee().getComponents().get(ComponentType.PROPULSORS).setEnergyDt(0);
         return;
      }

      evaluateMove();

      // Consume energy and resources amount linked to force generated and the kind of propulsor (propulsors are of varying efficiency)
      // TODO This is only a basic implementation
      if (!(this instanceof NoMoveOrder)) {
         getOrderee().getComponents().get(ComponentType.PROPULSORS).setEnergyDt(-getForce().length() / 40);
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
