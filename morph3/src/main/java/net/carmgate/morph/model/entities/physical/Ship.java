package net.carmgate.morph.model.entities.physical;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.events.entities.ship.ShipAdded;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.events.entities.ship.ShipHit;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.ship.move.MoveOrder;
import net.carmgate.morph.model.physics.ForceSource;

import org.slf4j.Logger;

public class Ship extends PhysicalEntity {

   public static final float MAX_PROPULSOR_FORCE = 20f;

   @Inject private World world;
   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private Logger LOGGER;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private OrderFactory orderFactory;

   private Player owner;
   private final Surroundings surroundings = new Surroundings();
   private final List<Order> orders = new ArrayList<>();
   private MoveOrder moveOrder;
   private float health;

   public void add(Order order) {
      order.setWorld(world);

      if (order instanceof MoveOrder) {
         LOGGER.debug("CloseIn order added: " + order);
         moveOrder = (MoveOrder) order;
      } else {
         LOGGER.debug("Attack order added: " + order);
         orders.add(order);
      }

      if (order instanceof ForceSource) {
         getForceSources().add((ForceSource) order);
      }
   }

   public Order getCurrentOrder() {
      if (orders.size() > 0) {
         return orders.get(0);
      }
      return null;
   }

   public float getHealth() {
      return health;
   }

   public MoveOrder getMoveOrder() {
      return moveOrder;
   }

   public Player getPlayer() {
      return owner;
   }

   public Surroundings getSurroundings() {
      return surroundings;
   }

   public void onShipAdded(@MObserves ShipAdded event) {
      // if (event.getShip() != this) {
      // if (event.getShip().getPlayer() != getPlayer()) {
      // final Attack attack = orderFactory.newInstance(OrderType.ATTACK);
      // // final CloseIn attack = orderFactory.newInstance(OrderType.CLOSE_IN);
      // // attack.setDistance(100);
      // attack.setOrderee(this);
      // attack.setTarget(event.getShip());
      // add(attack);
      // LOGGER.debug("Attack order added");
      // }
      // } else {
      // for (Ship ship : world.getShips()) {
      // if (ship.getPlayer() != getPlayer()) {
      // final Attack attack = orderFactory.newInstance(OrderType.ATTACK);
      // // final CloseIn attack = orderFactory.newInstance(OrderType.CLOSE_IN);
      // // attack.setDistance(100);
      // attack.setOrderee(this);
      // attack.setTarget(ship);
      // add(attack);
      // LOGGER.debug("Attack order added");
      // }
      // }
      // }
   }

   public void onShipHit(@MObserves ShipHit event) {
      if (event.getShip() == this && health > 0) {
         health -= event.getDamage();
         LOGGER.debug("Ship hit");
         if (health <= 0) {
            LOGGER.debug("Dying");
            final ShipDeath shipDead = worldEventFactory.newInstance(WorldEventType.SHIP_DEATH);
            shipDead.setDeadShip(this);
            worldEventMgr.fire(shipDead);
         }
      }
   }

   public void removeCurrentOrder() {
      orders.remove(getCurrentOrder());
   }

   public void setPlayer(Player owner) {
      this.owner = owner;
   }

   public void setHealth(float health) {
      this.health = health;
   }

}
