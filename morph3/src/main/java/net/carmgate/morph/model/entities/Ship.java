package net.carmgate.morph.model.entities;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.Vector2f;
import net.carmgate.morph.ui.renderers.Renderable;

public class Ship implements Renderable {

   private Vector2f pos;
   private float mass;
   private Player owner;

   public Ship(Vector2f pos) {
      this.pos = pos;
   }

   public Vector2f getPos() {
      return pos;
   }

   public void setPos(Vector2f pos) {
      this.pos = pos;
   }

   public float getMass() {
      return mass;
   }

   public void setMass(float mass) {
      this.mass = mass;
   }

   public Player getOwner() {
      return owner;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

}
