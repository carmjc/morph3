package net.carmgate.morph.model.entities.physical.ship;

import net.carmgate.morph.model.geometry.Vector2f;

public class ReadOnlyShip {

   private Ship ship;

   public ReadOnlyShip(Ship ship) {
      this.ship = ship;
   }

   public final Vector2f getPos() {
      return new Vector2f(ship.getPos());
   }

   public final Vector2f getSpeed() {
      return new Vector2f(ship.getSpeed());
   }

}
