package net.carmgate.morph.script.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.Asteroid;
import net.carmgate.morph.model.orders.OrderFactory;

import org.slf4j.Logger;

@Singleton
public class ScriptManager {

   @Inject private Logger LOGGER;
   @Inject private World world;
   @Inject private OrderFactory orderFactory;

   public void callScript(String script, Player player, Map<String, Object> inputs, Map<String, Object> outputs) {
      final ScriptEngineManager manager = new ScriptEngineManager();
      final ScriptEngine engine = manager.getEngineByName("nashorn");
      try {
         URL scriptResource = getClass().getResource("/shipScriptsFor" + player.getName() + "/" + script + ".js");
         if (scriptResource == null) {
            return;
         }

         InputStream in = getClass().getResourceAsStream("/shipScriptsFor" + player.getName() + "/" + script + ".js");
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         if (inputs != null) {
            for (Entry<String, Object> input : inputs.entrySet()) {
               engine.put(input.getKey(), input.getValue());
            }
         }
         engine.put("asteroids", world.getNonShipsPhysicalEntities().stream().filter(t -> t instanceof Asteroid).toArray());
         engine.put("orderFactory", orderFactory);
         engine.eval(reader);
         if (outputs != null) {
            for (String output : outputs.keySet()) {
               outputs.put(output, engine.get(output));
            }
         }
      } catch (final Exception e) {
         LOGGER.error("Cannot open script file: " + script, e);
      }
   }
}
