package net.carmgate.morph.model.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.entities.physical.ship.Ship;

public class Surroundings {
   private Map<Player, Set<Ship>> shipsByPlayer = new HashMap<>();

   public Map<Player, Set<Ship>> getShipsByPlayer() {
      return shipsByPlayer;
   }

   public void addShip(Ship ship) {
      Set<Ship> set = shipsByPlayer.get(ship.getPlayer());
      if (set == null) {
         set = new HashSet<>();
         shipsByPlayer.put(ship.getPlayer(), set);
      }
      set.add(ship);
   }

   public void addShips(List<Ship> ships) {
      for (Ship ship : ships) {
         addShip(ship);
      }
   }
}
