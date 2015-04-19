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
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.GameLoaded;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.ShipAdded;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.ui.renderers.api.Renderable;

import org.slf4j.Logger;

@Singleton
public class World {

   static String readFile(String path, Charset encoding)
         throws IOException {
      final byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
   }

   @Inject
   private Logger LOGGER;

   @Inject
   private Event<WorldEvent> worldEventMgr;

   @Inject
   private Event<GameLoaded> gameLoadedEvent;

   @Inject
   private OrderFactory orderFactory;

   @Inject
   private PhysicalEntityFactory entityFactory;

   private final List<Ship> ships = new ArrayList<>();
   // private final List<WorldUpdateListener> worldChangeListeners = new ArrayList<>();
   private final Set<PhysicalEntity> physicalEntities = new HashSet<>();
   private final Set<Renderable> animations = new HashSet<>();
   private final long initialTime;
   private long time = 0;

   // private final Set<ShipUpdated> worldEvents = new HashSet<>();

   public World() {
      initialTime = new Date().getTime();
   }

   public void add(Animation renderable) {
      animations.add(renderable);
   }

   public void add(Ship ship) {
      // TODO modify this so that ships have limited line of sight
      // Fill the ship
      ship.getPlayer().add(ship);

      // Update world
      ships.add(ship);
      physicalEntities.add(ship);

      // update surroundings of the awares
      worldEventMgr.fire(new ShipAdded(ship));
   }

   public Set<Renderable> getAnimations() {
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

   public void remove(Animation animation) {
      animations.remove(animation);
   }

   public void remove(Ship ship) {
      ships.remove(ship);
      physicalEntities.remove(ship);
      // TODO send event
   }

   public void updateTime() {
      time = new Date().getTime() - initialTime;
   }
}
