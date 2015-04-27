package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.List;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class Player {
   private String name;
   private List<Ship> ships = new ArrayList<>();
   private float[] color;

   public void add(Ship ship) {
      ships.add(ship);
   }

   public Player(String name) {
      this.name = name;
   }

   public List<Ship> getShips() {
      return ships;
   }

   public float[] getColor() {
      return color;
   }

   public void setColor(float[] color) {
      this.color = color;
   }
}
