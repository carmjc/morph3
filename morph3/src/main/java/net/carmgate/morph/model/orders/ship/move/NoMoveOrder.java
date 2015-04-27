package net.carmgate.morph.model.orders.ship.move;

import net.carmgate.morph.model.geometry.Vector2f;

public class NoMoveOrder extends MoveOrder {

   @Override
   public Vector2f getForce() {
      return Vector2f.NULL;
   }

   @Override
   protected void evaluateMove() {
   }

}
