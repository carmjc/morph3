package net.carmgate.morph.model.orders.ship;

import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.orders.Order;

public class Attack extends Order {

   private Ship ship;

   public Attack(Ship ship) {
      this.ship = ship;
   }

   @Override
   public void evaluate(long nextEvaluationInMillis) {

   }

}
