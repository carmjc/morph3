package net.carmgate.morph.model.entities.physical.ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.events.WorldEventFactory;
import net.carmgate.morph.events.WorldEventType;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.entities.ship.ShipHit;
import net.carmgate.morph.events.mgt.MEvent;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.orders.Order;
import net.carmgate.morph.orders.ship.Unique;
import net.carmgate.morph.orders.ship.action.ActionOrder;
import net.carmgate.morph.orders.ship.move.MoveOrder;
import net.carmgate.morph.script.util.ScriptManager;

import org.slf4j.Logger;

public class Ship extends PhysicalEntity {

   public static final float MAX_PROPULSOR_FORCE = 20f;

   @Inject private World world;
   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private Logger LOGGER;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private ScriptManager scriptManager;

   private Player owner;
   private final Surroundings surroundings = new Surroundings();
   private Order actionOrder;
   private MoveOrder moveOrder;
   private final List<Order> bgOrders = new ArrayList<>();
   private float durability;
   private final Map<ComponentType, Component> components = new HashMap<>();
   private final Map<ComponentType, Float> componentsComposition = new HashMap<>();

   // internal economics
   private float energy;
   private float energydt; // energy variation d(energy)/dt
   private float resources;
   private float resourcesdt; // resources variation d(resources)/dt
   private float integrity = 1;
   private float integrityDt; // integrity variation d(integrity)/dt

   public Vector2f debug1 = new Vector2f();
   public Vector2f debug2 = new Vector2f();
   public Vector2f debug3 = new Vector2f();
   public Vector2f debug4 = new Vector2f();

   private boolean forceStop;

   public void add(Component component, float compositionContribution) {
      component.setShip(this);
      ComponentKind componentKind = component.getClass().getAnnotation(ComponentKind.class);
      getComponents().put(componentKind.value(), component);

      // update components composition
      for (Entry<ComponentType, Float> entry : componentsComposition.entrySet()) {
         entry.setValue(entry.getValue() * (1 - compositionContribution));
      }
      Float componentCurrentContribution = componentsComposition.get(componentKind.value());
      if (componentCurrentContribution == null) {
         componentsComposition.put(componentKind.value(), compositionContribution);
      } else {
         componentsComposition.put(componentKind.value(), componentCurrentContribution + compositionContribution);
      }
   }

   public void add(Order order) {
      order.setWorld(world);

      if (order instanceof MoveOrder) {
         if (moveOrder != null) {
            moveOrder.onRemoveOrder();
            if (moveOrder instanceof ForceSource) {
               getForceSources().remove(moveOrder);
            }
         }
         moveOrder = (MoveOrder) order;
         LOGGER.debug("Move order added: " + order); //$NON-NLS-1$
      } else if (order instanceof ActionOrder) {
         if (actionOrder != null) {
            actionOrder.onRemoveOrder();
            if (actionOrder instanceof ForceSource) {
               getForceSources().remove(actionOrder);
            }
         }
         actionOrder = order;
         LOGGER.debug("Action order added: " + order); //$NON-NLS-1$
      } else {
         if (order.getClass().isAnnotationPresent(Unique.class)) {
            for (Order uniqueOrder : bgOrders) {
               if (uniqueOrder.getClass().equals(order.getClass())) {
                  return;
               }
            }
         }
         bgOrders.add(order);
         LOGGER.debug("Background order added: " + order); //$NON-NLS-1$
      }

      if (order instanceof ForceSource) {
         getForceSources().add((ForceSource) order);
      }
   }

   public Order getActionOrder() {
      return actionOrder;
   }

   public float getIntegrity() {
      return integrity;
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

   // FIXME This will occur too regularly, event should be used only for non periodic events
   public void onShipHit(@MObserves ShipHit event) {
      if (event.getShip() == this && integrity > 0) {
         integrity -= event.getDamage() / durability;
         if (integrity <= 0) {
            LOGGER.debug("Dying"); //$NON-NLS-1$
            final ShipDeath shipDead = worldEventFactory.newInstance(WorldEventType.SHIP_DEATH);
            shipDead.setDeadShip(this);
            worldEventMgr.fire(shipDead);
         } else {
            HashMap<String, Object> inputs = new HashMap<>();
            inputs.put("self", this);
            inputs.put("damage", event.getDamage());
            inputs.put("aggressor", event.getAggressor());
            scriptManager.callScript("onSelfShipHit", getPlayer(), inputs, null);
         }
      }
   }

   public void onShipDeath(@MObserves ShipDeath shipDeath) {
      if (shipDeath.getShip() != this) {
         HashMap<String, Object> inputs = new HashMap<>();
         inputs.put("self", this);
         inputs.put("ship", new ReadOnlyShip(shipDeath.getShip()));
         scriptManager.callScript("onOtherShipDeath", getPlayer(), inputs, null);
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

   public void setIntegrity(float integrity) {
      this.integrity = integrity;
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

   public List<Order> getBgOrders() {
      return bgOrders;
   }

   public float getDurability() {
      return durability;
   }

   public void setDurability(float durability) {
      this.durability = durability;
   }

   public float getIntegrityDt() {
      return integrityDt;
   }

   public void setIntegrityDt(float integrityDt) {
      this.integrityDt = integrityDt;
   }

   public void addIntegrity(float integrityDelta) {
      integrity += integrityDelta;
   }

   public Map<ComponentType, Float> getComponentsComposition() {
      return componentsComposition;
   }
}
