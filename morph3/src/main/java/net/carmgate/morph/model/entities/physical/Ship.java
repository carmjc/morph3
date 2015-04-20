package net.carmgate.morph.model.entities.physical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.events.DeadShip;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.api.Renderable;

import org.slf4j.Logger;

public class Ship implements Renderable, PhysicalEntity {

   @Inject private World world;
   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private Logger LOGGER;
   @Inject private WorldEventFactory worldEventFactory;

   private final Vector2f pos = new Vector2f();
   private final Vector2f speed = new Vector2f();
   private float mass;
   private Player owner;
   private final Surroundings surroundings = new Surroundings();
   private final Set<ForceSource> forceSources = new HashSet<>();
   private final List<Order> orders = new ArrayList<>();
   private float health;

   public void add(Order order) {
      order.setWorld(world);

      orders.add(order);

      if (order instanceof ForceSource) {
         forceSources.add((ForceSource) order);
      }
   }

   public Order getCurrentOrder() {
      if (orders.size() > 0) {
         return orders.get(0);
      }
      return null;
   }

   @Override
   public Set<ForceSource> getForceSources() {
      return forceSources;
   }

   public float getHealth() {
      return health;
   }

   @Override
   public float getMass() {
      return mass;
   }

   public Player getPlayer() {
      return owner;
   }

   @Override
   public Vector2f getPos() {
      return pos;
   }

   @Override
   public Vector2f getSpeed() {
      return speed;
   }

   public Surroundings getSurroundings() {
      return surroundings;
   }

   public void init(Vector2f pos, float health) {
      this.health = health;
      this.pos.copy(pos);
   }

   public void onShipHit(@MObserves ShipHit event) {
      if (event.getShip() == this) {
         health -= event.getDamage();
         if (health <= 0) {
            DeadShip shipDead = worldEventFactory.newInstance(WorldEventType.SHIP_DEAD);
            shipDead.setDeadShip(this);
            worldEventMgr.fire(shipDead);
         }
      }
   }

   public void setMass(float mass) {
      this.mass = mass;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

   public void removeCurrentOrder() {
      orders.remove(getCurrentOrder());
   }

}
