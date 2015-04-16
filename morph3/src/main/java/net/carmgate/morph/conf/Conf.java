package net.carmgate.morph.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class Conf {

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

   public float getFloatProperty(String key) {
      return Float.parseFloat(prop.getProperty(key));
   }

   public int getIntProperty(String key) {
      return Integer.parseInt(prop.getProperty(key));
   }

   public String getProperty(String key) {
      return prop.getProperty(key);
   }
}
