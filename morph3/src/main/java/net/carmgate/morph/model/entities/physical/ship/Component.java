package net.carmgate.morph.model.entities.physical.ship;

public abstract class Component {
   private float energyDt;
   private float resourcesDt;

   private final Ship ship;

   public float getEnergyDt() {
      return energyDt;
   }

   public float getResourcesDt() {
      return resourcesDt;
   }

   public void setEnergyDt(float energyDt) {
      this.energyDt = energyDt; // FIXME We should add an efficiency factor coming from the real component
   }

   public void setResourcesDt(float resourcesDt) {
      this.resourcesDt = resourcesDt; // FIXME We should add an efficiency factor coming from the real component
   }

   public Component(Ship ship) {
      this.ship = ship;
   }

   public Ship getShip() {
      return ship;
   }
}
