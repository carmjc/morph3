package net.carmgate.morph.model.entities.physical;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public enum PhysicalEntityType {
   SHIP(Ship.class),
   ASTEROID(Asteroid.class);

   private final Class<? extends PhysicalEntity> clazz;

   private PhysicalEntityType(Class<? extends PhysicalEntity> clazz) {
      this.clazz = clazz;
   }

   public Class<? extends PhysicalEntity> getClazz() {
      return clazz;
   }
}
