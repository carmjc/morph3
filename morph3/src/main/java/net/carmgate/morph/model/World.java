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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.model.entities.Animation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.entities.ShipUpdatedListener;
import net.carmgate.morph.model.events.ShipAdded;
import net.carmgate.morph.model.events.ShipDead;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.ShipUpdated;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.events.WorldUpdateListener;
import net.carmgate.morph.ui.renderers.api.Renderable;

import org.slf4j.Logger;

@Singleton
public class World implements ShipUpdatedListener {

   static String readFile(String path, Charset encoding)
         throws IOException {
      final byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
   }

   @Inject
   private Logger LOGGER;
   private final List<Ship> ships = new ArrayList<>();
   private final List<WorldUpdateListener> worldChangeListeners = new ArrayList<>();
   private final Set<PhysicalEntity> physicalEntities = new HashSet<>();
   private final Set<Renderable> animations = new HashSet<>();
   private final long initialTime;
   private long time = 0;
   private final Set<ShipUpdated> worldEvents = new HashSet<>();

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
      ship.setWorld(this);
      ship.addShipUpdatedListener(this);

      // Update world
      ships.add(ship);
      physicalEntities.add(ship);

      // update surroundings of the awares
      worldChangeListeners.add(ship);
      for (final WorldUpdateListener listener : worldChangeListeners) {
         listener.onWorldUpdate(new ShipAdded(ship, this));
      }
   }

   public void fireEvent(ShipUpdated event) {
      worldEvents.add(event);
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
      final ScriptEngineManager manager = new ScriptEngineManager();
      final ScriptEngine engine = manager.getEngineByName("nashorn");
      try {
         final FileReader reader = new FileReader(getClass().getResource("/model-init.js").getPath());
         engine.put("model", this);
         engine.eval(reader);
      } catch (final Exception e) {
         LOGGER.error("Cannot open init file", e);
      }
   }

   @Override
   public void onShipUpdated(ShipUpdated event) {
      if (event instanceof ShipHit) {
         worldChangeListeners.forEach(listener -> {
            listener.onWorldUpdate(event);
         });
      }
      if (event instanceof ShipDead) {
         worldChangeListeners.forEach(listener -> {
            listener.onWorldUpdate(event);
         });
         ships.remove(event.getShip());
      }
   }

   public void remove(Animation animation) {
      animations.remove(animation);
   }

   public void remove(Ship ship) {
      ships.remove(ship);
      worldChangeListeners.remove(ship);
      physicalEntities.remove(ship);
   }

   public void updateTime() {
      time = new Date().getTime() - initialTime;
   }

   @Inject
   Instance<WorldEvent> worldEventInstances;

   public <T> T createWorldEvent(String name) {
      for (WorldEvent event : worldEventInstances) {
         if (event.getClass().getName().equals(name)) {
            return (T) event;
         }
      }
      return null;
   }
}
