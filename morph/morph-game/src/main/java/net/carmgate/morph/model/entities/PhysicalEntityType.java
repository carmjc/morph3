package net.carmgate.morph.model.entities;

import net.carmgate.morph.model.entities.ship.Ship;

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
