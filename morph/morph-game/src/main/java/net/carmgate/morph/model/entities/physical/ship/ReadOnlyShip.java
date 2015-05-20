package net.carmgate.morph.model.entities.physical.ship;

import net.carmgate.morph.model.geometry.Vector2f;

/**
 * This class is used to provide the script writers a safe clone of the ship to use
 */
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
