package net.carmgate.morph.model.events;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Ship;

public class ShipAdded implements WorldChanged {
   private Ship ship;
   private World world;

   public ShipAdded(Ship ship, World world) {
      this.ship = ship;
      this.world = world;
   }

   public Ship getShip() {
      return ship;
   }

   public World getWorld() {
      return world;
   }
}
