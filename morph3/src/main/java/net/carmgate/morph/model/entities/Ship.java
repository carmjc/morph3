package net.carmgate.morph.model.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.api.WorldChangeListener;
import net.carmgate.morph.model.events.ShipAdded;
import net.carmgate.morph.model.events.WorldChanged;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.api.Renderable;

public class Ship implements Renderable, WorldChangeListener, PhysicalEntity {

   private Vector2f pos = new Vector2f();
   private Vector2f speed = new Vector2f();
   private float mass;
   private Player owner;
   private Surroundings surroundings = new Surroundings();
   private Order currentGoal;
   private Set<ForceSource> forceSources = new HashSet<>();
   private List<Order> orders = new ArrayList<>();

   public Ship(Vector2f pos) {
      this.pos.copy(pos);
   }

   @Override
   public Vector2f getPos() {
      return pos;
   }

   @Override
   public float getMass() {
      return mass;
   }

   public void setMass(float mass) {
      this.mass = mass;
   }

   public Player getPlayer() {
      return owner;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

   @Override
   public void onWorldChanged(WorldChanged event) {
      if (event instanceof ShipAdded) {
         Ship ship = ((ShipAdded) event).getShip();
         if (ship != this) {
            // add the ship
            surroundings.addShip(ship);
         } else {
            // add the other ships
            // TODO we should do this otherwise
            for (Ship tmpShip : ((ShipAdded) event).getWorld().getShips()) {
               if (tmpShip != this) {
                  surroundings.addShip(tmpShip);
               }
            }
         }
      }
   }

   public Surroundings getSurroundings() {
      return surroundings;
   }

   public void add(Order order) {
      orders.add(order);

      if (order instanceof ForceSource) {
         forceSources.add((ForceSource) order);
      }
   }

   @Override
   public Vector2f getSpeed() {
      return speed;
   }

   @Override
   public Set<ForceSource> getForceSources() {
      return forceSources;
   }

   public Order getCurrentGoal() {
      return currentGoal;
   }

}
