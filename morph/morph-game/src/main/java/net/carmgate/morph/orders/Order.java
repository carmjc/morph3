package net.carmgate.morph.orders;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;

public abstract class Order {

   private World world;
   private long nextEvalTime = 0;
   private Ship orderee;
   private boolean done = false;
   private OrderType orderType;

   public void eval() {
      if (world.getTime() > nextEvalTime) {
         evaluate();
      }
   }

   protected abstract void evaluate();

   public long getNextEvalTime() {
      return nextEvalTime;
   }

   public Ship getOrderee() {
      return orderee;
   }

   public World getWorld() {
      return world;
   }

   public boolean isDone() {
      return done;
   }

   protected void setDone(boolean done) {
      this.done = done;
   }

   protected void setNextEvalTime(long nextEvalTime) {
      this.nextEvalTime = nextEvalTime;
   }

   public void setOrderee(Ship orderee) {
      this.orderee = orderee;
   }

   public void setWorld(World world) {
      this.world = world;
      nextEvalTime = world.getTime();
   }

   public abstract ComponentType[] getComponentTypes();

   public abstract int getCriticity();

   public OrderType getOrderType() {
      return orderType;
   }

   public void setOrderType(OrderType orderType) {
      this.orderType = orderType;
   }

   public abstract void onRemoveOrder();

}
