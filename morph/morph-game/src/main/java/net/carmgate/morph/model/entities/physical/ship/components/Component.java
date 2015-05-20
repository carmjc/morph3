package net.carmgate.morph.model.entities.physical.ship.components;

import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public abstract class Component {
   private float energyDt = 0;
   private float resourcesDt = 0;
   private float integrityDt = 0;
   private boolean active;
   private boolean famished; // FIXME rename this
   private Animation animation;
   private final Holder<Ship> shipHolder = new Holder<>();
   private final Holder<PhysicalEntity> targetHolder = new Holder<>();

   public Component() {
      if (getClass().isAnnotationPresent(AlwaysActive.class)) {
         setActive(true);
      }
   }

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

   public Ship getShip() {
      return shipHolder.get();
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public Animation getAnimation() {
      return animation;
   }

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public void setShip(Ship ship) {
      shipHolder.set(ship);
   }

   public PhysicalEntity getTarget() {
      return targetHolder.get();
   }

   public void setTarget(PhysicalEntity target) {
      targetHolder.set(target);
   }

   public Holder<Ship> getShipHolder() {
      return shipHolder;
   }

   public Holder<PhysicalEntity> getTargetHolder() {
      return targetHolder;
   }

   public boolean isFamished() {
      return famished;
   }

   public void setFamished(boolean famished) {
      this.famished = famished;
   }

   public float getIntegrityDt() {
      return integrityDt;
   }

   public void setIntegrityDt(float integrityDt) {
      this.integrityDt = integrityDt;
   }

   public float getMaxStoredEnergy() {
      return 0;
   }

   public float getMaxStoredResources() {
      return 0;
   }
}
