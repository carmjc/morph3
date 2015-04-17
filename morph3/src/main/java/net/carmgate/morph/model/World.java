package net.carmgate.morph.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.entities.ShipAdded;
import net.carmgate.morph.model.entities.WorldChangeListener;

import org.slf4j.Logger;

@Singleton
public class World {

   @Inject
   private Logger LOGGER;

   private final List<Ship> ships = new ArrayList<>();
   private final List<WorldChangeListener> worldChangeListeners = new ArrayList<>();
   private final Set<PhysicalEntity> physicalEntities = new HashSet<>();

   public void add(Ship ship) {
      // TODO modify this so that ships have limited line of sight
      // Fill the ship
      ship.getPlayer().add(ship);

      // Update world
      ships.add(ship);
      physicalEntities.add(ship);

      // update surroundings of the awares
      worldChangeListeners.add(ship);
      for (WorldChangeListener listener : worldChangeListeners) {
         listener.onWorldChanged(new ShipAdded(ship, this));
      }
   }

   /**
    * Do not use this method if you intend to modify this list.
    */
   public List<Ship> getShips() {
      return ships;
   }

   @PostConstruct
   private void init() {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("nashorn");
      try {
         FileReader reader = new FileReader(getClass().getResource("/model-init.js").getPath());
         engine.put("model", this);
         engine.eval(reader);
      } catch (Exception e) {
         LOGGER.error("Cannot open init file", e);
      }

   }

   public void removeShip(Ship ship) {
      ships.remove(ship);
      worldChangeListeners.remove(ship);
      physicalEntities.remove(ship);
   }

   public Set<PhysicalEntity> getPhysicalEntities() {
      return physicalEntities;
   }
}
