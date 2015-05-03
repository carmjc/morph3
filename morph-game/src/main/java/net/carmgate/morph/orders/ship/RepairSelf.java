package net.carmgate.morph.orders.ship;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.orders.Order;

import org.slf4j.Logger;

@Unique
public class RepairSelf extends Order {

   @Inject private Logger LOGGER;
   @Inject private World world;

   private long lastUpdateTime;

   @PostConstruct
   private void init() {
      lastUpdateTime = world.getTime();
   }

   @Override
   protected void evaluate() {
      getOrderee().setIntegrity(getOrderee().getIntegrity() + ((float) world.getTime() - lastUpdateTime) / 100000);
      if (getOrderee().getIntegrity() >= 1) {
         getOrderee().setIntegrity(1);
         setDone(true);
      }
      // LOGGER.debug(getOrderee().getIntegrity() + " - " + ((float) world.getTime() - lastUpdateTime) / 100000 + "-" + lastUpdateTime + "/" + world.getTime());
      lastUpdateTime = world.getTime();
   }

   @Override
   public ComponentType[] getComponentTypes() {
      return new ComponentType[] { ComponentType.REPAIRER };
   }

   @Override
   public int getCriticity() {
      return 0;
   }

}
