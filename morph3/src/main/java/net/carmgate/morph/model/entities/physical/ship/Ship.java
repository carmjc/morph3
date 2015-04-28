package net.carmgate.morph.model.entities.physical.ship;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.events.entities.ship.ShipAdded;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.events.entities.ship.ShipHit;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.ship.action.ActionOrder;
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
   private Order actionOrder;
   private MoveOrder moveOrder;
   private float health;
   private final Map<ComponentType, Component> components = new HashMap<>();

   // internal economics
   private float energy;
   private float energydt; // energy variation d(energy)/dt
   private float resources;
   private float resourcesdt; // resources variation d(resources)/dt

   public Vector2f debug1 = new Vector2f();
   public Vector2f debug2 = new Vector2f();
   public Vector2f debug3 = new Vector2f();
   public Vector2f debug4 = new Vector2f();

   private boolean forceStop;

   public void add(Order order) {
      order.setWorld(world);

      if (order instanceof MoveOrder) {
         LOGGER.debug("CloseIn order added: " + order);
         if (moveOrder != null && moveOrder instanceof ForceSource) {
            getForceSources().remove(moveOrder);
         }
         moveOrder = (MoveOrder) order;
      } else if (order instanceof ActionOrder) {
         LOGGER.debug("Attack order added: " + order);
         if (actionOrder != null && actionOrder instanceof ForceSource) {
            getForceSources().remove(actionOrder);
         }
         actionOrder = order;
      } else {
         LOGGER.error("Could not add unknown order type: " + order.getClass().getSimpleName());
         return;
      }

      if (order instanceof ForceSource) {
         getForceSources().add((ForceSource) order);
      }
   }

   public Order getActionOrder() {
      return actionOrder;
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

   public void removeActionOrder() {
      if (actionOrder instanceof ForceSource) {
         getForceSources().remove(actionOrder);
      }
      actionOrder = null;
   }

   public void setPlayer(Player owner) {
      this.owner = owner;
   }

   public void setHealth(float health) {
      this.health = health;
   }

   public float getEnergy() {
      return energy;
   }

   public void setEnergy(float energy) {
      this.energy = energy;
   }

   public void addEnergy(float energy) {
      this.energy += energy;
   }

   public float getEnergyDt() {
      return energydt;
   }

   public void setEnergyDt(float energydt) {
      this.energydt = energydt;
   }

   public void addEnergydt(float energydt) {
      this.energydt += energydt;
   }

   public float getResources() {
      return resources;
   }

   public void setResources(float resources) {
      this.resources = resources;
   }

   public void addResources(float resources) {
      this.resources += resources;
   }

   public float getResourcesDt() {
      return resourcesdt;
   }

   public void setResourcesDt(float resourcesdt) {
      this.resourcesdt = resourcesdt;
   }

   public void addResourcesDt(float resources) {
      this.resources += resources;
   }

   public Map<ComponentType, Component> getComponents() {
      return components;
   }

   public void setForceStop(boolean forceStop) {
      this.forceStop = forceStop;
   }

   public boolean isForceStop() {
      return forceStop;
   }
}
