package net.carmgate.morph.model.orders;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.ComponentType;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public abstract class Order {

   private World world;
   private long nextEvalTime = 0;
   private Ship orderee;
   private boolean done = false;

   public Order() {
   }

   public void eval() {
      if (world.getTime() > nextEvalTime) {
         float energyDt = 0;
         float resourcesDt = 0;
         for (ComponentType compType : getComponentTypes()) {
            energyDt += getOrderee().getComponents().get(compType).getEnergyDt();
            resourcesDt += getOrderee().getComponents().get(compType).getResourcesDt();
         }
         if (getOrderee().getEnergy() > energyDt && getOrderee().getResources() > resourcesDt) {
            evaluate();
         }
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

}
