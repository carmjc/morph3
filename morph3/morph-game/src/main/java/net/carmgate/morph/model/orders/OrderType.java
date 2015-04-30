package net.carmgate.morph.model.orders;

import net.carmgate.morph.model.orders.ship.Flee;
import net.carmgate.morph.model.orders.ship.RepairSelf;
import net.carmgate.morph.model.orders.ship.action.Attack;
import net.carmgate.morph.model.orders.ship.action.MineAsteroid;
import net.carmgate.morph.model.orders.ship.move.CloseIn;
import net.carmgate.morph.model.orders.ship.move.NoMoveOrder;

public enum OrderType {
   ATTACK(Attack.class),
   FLEE(Flee.class),
   CLOSE_IN(CloseIn.class),
   NO_MOVE(NoMoveOrder.class),
   MINE_ASTEROID(MineAsteroid.class),
   REPAIR_SELF(RepairSelf.class);

   private final Class<? extends Order> clazz;

   OrderType(Class<? extends Order> clazz) {
      this.clazz = clazz;
   }

   public Class<? extends Order> getClazz() {
      return clazz;
   }
}