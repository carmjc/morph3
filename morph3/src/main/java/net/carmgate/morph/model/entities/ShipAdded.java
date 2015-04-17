package net.carmgate.morph.model.entities;

import net.carmgate.morph.model.World;

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
