package net.carmgate.morph.model.orders;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.Ship;

public abstract class Order {

   private World world;
   private long nextEvalTime = 0;
   protected Ship orderee;
   private boolean done = false;

   public Order() {
   }

   public void setOrderee(Ship orderee) {
      this.orderee = orderee;
   }

   public void eval() {
      if (world.getTime() > nextEvalTime) {
         evaluate();
      }
   }

   protected abstract void evaluate();

   public long getNextEvalTime() {
      return nextEvalTime;
   }

   public World getWorld() {
      return world;
   }

   protected void setNextEvalTime(long nextEvalTime) {
      this.nextEvalTime = nextEvalTime;
   }

   public void setWorld(World world) {
      this.world = world;
      nextEvalTime = world.getTime();
   }

   public boolean isDone() {
      return done;
   }

   protected void setDone(boolean done) {
      this.done = done;
   }

}
