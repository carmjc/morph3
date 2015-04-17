package net.carmgate.morph.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.model.entities.Ship;

import org.slf4j.Logger;

@Singleton
public class Model {

   @Inject
   private Logger LOGGER;

   private final List<Ship> ships = new ArrayList<>();
   private final List<Player> players = new ArrayList<>();

   public void add(Ship ship) {
      ships.add(ship);
      ship.getOwner().add(ship);
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
   }
}
