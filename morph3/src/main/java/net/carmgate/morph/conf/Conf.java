package net.carmgate.morph.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class Conf {

   public static enum ConfItem {
      MORPH_ARMOR_HITPOINTS_LEVEL1("morph.armor.hitpoints.level1"),
      MORPH_LASER_MAXXPPERHIT("morph.laser.maxXpPerHit"),
      MORPH_LASER_MAXXPPERHIT_FOR_OVERMIND("morph.laser.maxXpPerHit.forOvermind"),
      MORPH_LASER_MAXDAMAGELEVEL1("morph.laser.maxDamageLevel1"),
      MORPH_MAXXPLEVEL1("morph.maxXpLevel1"),
      MORPH_SIMPLEPROPULSOR_MAXANGLESPEEDPERMASSUNIT("morph.simplePropulsor.maxAngleSpeedPerMassUnit"),
      MORPH_SIMPLEPROPULSOR_MAXFORCE("morph.simplePropulsor.maxForce"),
      MORPH_SIMPLEPROPULSOR_MAXFORCE_FACTORPERLEVEL("morph.simplePropulsor.maxForce.factorPerLevel"),
      MORPH_SIMPLEPROPULSOR_MAXSPEED("morph.simplePropulsor.maxSpeed"),
      MORPH_SIMPLEPROPULSOR_MAXSPEED_FACTORPERLEVEL("morph.simplePropulsor.maxSpeed.factorPerLevel"),
      MORPH_SIMPLEPROPULSOR_MAXXPPERSECOND("morph.simplePropulsor.maxXpPerSecond"),
      MORPH_SIMPLEPROPULSOR_MAXXPPERSECOND_FOR_OVERMIND("morph.simplePropulsor.maxXpPerSecond.forOvermind"),
      SHIP_HEALTH_PER_MASS("ship.healthPerMass"),
      SHIP_NORADAR_DETECTION_RANGE("ship.noradar.detectionRange"),
      SHIP_TRAIL_NUMBEROFSEGMENTS("ship.trail.numberOfSegments"),
      SHIP_TRAIL_UPDATEINTERVAL("ship.trail.updateInterval"),
      ZOOM_VARIATIONFACTOR("zoom.variationFactor"),
      ZOOM_MAX("zoom.max");

      private final String key;

      ConfItem(String key) {
         this.key = key;
      }

      public String getKey() {
         return key;
      }
   }

   @Inject
   private Logger LOGGER;

   private final Properties prop;

   private Conf() {
      prop = new Properties();
      try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
         prop.load(in);
      } catch (final IOException e) {
         LOGGER.error("Exception raised loading properties", e);
      }
   }

   public float getFloatProperty(ConfItem confItem) {
      return Float.parseFloat(prop.getProperty(confItem.getKey()));
   }

   public float getFloatProperty(String key) {
      return Float.parseFloat(prop.getProperty(key));
   }

   public int getIntProperty(ConfItem confItem) {
      return Integer.parseInt(prop.getProperty(confItem.getKey()));
   }

   public int getIntProperty(String key) {
      return Integer.parseInt(prop.getProperty(key));
   }

   public String getProperty(ConfItem confItem) {
      return prop.getProperty(confItem.getKey());
   }

   public String getProperty(String key) {
      return prop.getProperty(key);
   }
}
