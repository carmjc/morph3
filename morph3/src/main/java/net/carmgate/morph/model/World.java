package net.carmgate.morph.model;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.GameLoaded;
import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MEventManager;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.physical.PhysicalEntityType;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldEventFactory;
import net.carmgate.morph.model.events.WorldEventType;
import net.carmgate.morph.model.events.animations.AnimationStart;
import net.carmgate.morph.model.events.entities.ship.PhysicalEntityToBeRemoved;
import net.carmgate.morph.model.events.entities.ship.ShipAdded;
import net.carmgate.morph.model.events.entities.ship.ShipDeath;
import net.carmgate.morph.model.orders.OrderFactory;

import org.slf4j.Logger;

@Singleton
public class World {

   public static String readFile(String path, Charset encoding)
         throws IOException {
      final byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
   }

   @Inject private Logger LOGGER;
   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private Event<GameLoaded> gameLoadedEvent;
   @Inject private OrderFactory orderFactory;
   @Inject private PhysicalEntityFactory entityFactory;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private MEventManager eventManager;

   private final List<Ship> ships = new ArrayList<>();
   private final List<PhysicalEntity> nonShipsPhysicalEntities = new ArrayList<>();
   // private final List<WorldUpdateListener> worldChangeListeners = new ArrayList<>();
   private final Set<PhysicalEntity> physicalEntities = new HashSet<>();
   private final Set<Animation> animations = new HashSet<>();
   private long lastUpdateTime = 0;
   private long time = 0;
   private float timeFactor = 1f;
   private boolean timeFrozen = true;


   // private final Set<ShipUpdated> worldEvents = new HashSet<>();

   public void add(Animation renderable) {
      animations.add(renderable);
   }

   public void add(PhysicalEntity entity) {
      if (entity instanceof Ship) {
         add((Ship) entity);
         return;
      }

      nonShipsPhysicalEntities.add(entity);
      physicalEntities.add(entity);
   }

   private void add(Ship ship) {
      // TODO modify this so that ships have limited line of sight
      // Fill the ship
      ship.getPlayer().add(ship);

      // Update world
      ships.add(ship);
      physicalEntities.add(ship);

      // update surroundings of the awares
      final ShipAdded shipAdded = worldEventFactory.newInstance(WorldEventType.SHIP_ADDED);
      shipAdded.setAddedShip(ship);
      worldEventMgr.fire(shipAdded);
   }

   public Set<Animation> getAnimations() {
      return animations;
   }

   public Set<PhysicalEntity> getPhysicalEntities() {
      return physicalEntities;
   }

   /**
    * Do not use this method if you intend to modify this list.
    */
   public List<Ship> getShips() {
      return ships;
   }

   public long getTime() {
      return time;
   }

   @PostConstruct
   private void init() {
      eventManager.scanAndRegister(this);

      new Thread((Runnable) () -> {
         final ScriptEngineManager manager = new ScriptEngineManager();
         final ScriptEngine engine = manager.getEngineByName("nashorn");
         try {
            final FileReader reader = new FileReader(getClass().getResource("/model-init.js").getPath());
            engine.put("world", World.this);
            engine.put("orderFactory", orderFactory);
            engine.put("entityFactory", entityFactory);
            engine.eval(reader);
            gameLoadedEvent.fire(new GameLoaded());
         } catch (final Exception e) {
            LOGGER.error("Cannot open init file", e);
         }
      }, "model init").start();
   }

   protected void onAnimationStart(@MObserves AnimationStart animationStart) {
      animations.add(animationStart.getAnimation());
   }

   protected void onShipDeath(@MObserves ShipDeath shipDeath) {
      LOGGER.debug("Ship death: " + shipDeath.getShip());
      remove(shipDeath.getShip());

      final Ship ship = entityFactory.newInstance(PhysicalEntityType.SHIP);
      ship.setPlayer(shipDeath.getShip().getPlayer());
      final Random random = new Random();
      ship.setMass(random.nextFloat() / 2 + 0.5f);
      ship.getPos().copy(random.nextFloat() * 400 - 200, random.nextFloat() * 400 - 200);
      ship.setHealth(10);
      add(ship);
   }

   protected void onEntityRemovel(@MObserves PhysicalEntityToBeRemoved event) {
      LOGGER.debug("Removing " + event.getEntity().getClass().getName());
      remove(event.getEntity());
   }

   private void remove(PhysicalEntity entity) {
      if (entity instanceof Ship) {
         remove((Ship) entity);
         return;
      }

      nonShipsPhysicalEntities.remove(entity);
      physicalEntities.remove(entity);
   }

   public void remove(Animation animation) {
      animations.remove(animation);
   }

   private void remove(Ship ship) {
      ships.remove(ship);
      physicalEntities.remove(ship);
      // TODO send event
   }

   public void updateTime() {
      long newUpdateTime = new Date().getTime();
      if (lastUpdateTime != 0) {
         float timeFactor = timeFrozen ? 0 : this.timeFactor;
         time += (newUpdateTime - lastUpdateTime) * timeFactor;
      }
      lastUpdateTime = newUpdateTime;
   }

   public List<PhysicalEntity> getNonShipsPhysicalEntities() {
      return nonShipsPhysicalEntities;
   }

   public void setTimeFactor(int timeFactor) {
      this.timeFactor = timeFactor;
   }

   public boolean isTimeFrozen() {
      return timeFrozen;
   }

   public void toggleTimeFrozen() {
      timeFrozen = !timeFrozen;
   }

}
