package net.carmgate.morph.model.orders.ship.move;

import net.carmgate.morph.model.orders.Order;

public abstract class MoveOrder extends Order {

   private Order parentOrder;

   @Override
   protected void evaluate() {
      // TODO Auto-generated method stub

   }

   public Order getParentOrder() {
      return parentOrder;
   }

   public void setParentOrder(Order parentOrder) {
      this.parentOrder = parentOrder;
   }

}
