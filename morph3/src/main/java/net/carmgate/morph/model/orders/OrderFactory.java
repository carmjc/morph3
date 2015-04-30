package net.carmgate.morph.model.orders;

import java.lang.reflect.Field;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.eventmgt.MEventManager;
import net.carmgate.morph.model.entities.physical.ship.ReadOnlyShip;
import net.carmgate.morph.model.entities.physical.ship.Ship;

import org.slf4j.Logger;

@Singleton
public class OrderFactory {

   @Inject private Logger LOGGER;
   @Inject private Instance<Order> orders;
   @Inject private MEventManager eventManager;

   @SuppressWarnings("unchecked")
   public <U extends Order> U newInstance(OrderType orderType, Ship orderee) {
      final U u = (U) orders.select(orderType.getClazz()).get();
      eventManager.scanAndRegister(u);
      u.setOrderee(orderee);
      u.setOrderType(orderType);
      return u;
   }

   public <U extends Order> U newInstance(String orderType, Ship orderee) {
      OrderType orderTypeValue = OrderType.valueOf(orderType);
      if (orderTypeValue == null) {
         return null;
      }

      return newInstance(orderTypeValue, orderee);
   }

   public <U extends Order> U newInstance(String orderType, ReadOnlyShip orderee) {
      U u = null;
      Field shipField;
      try {
         shipField = ReadOnlyShip.class.getDeclaredField("ship");
         shipField.setAccessible(true);
         u = newInstance(orderType, (Ship) shipField.get(orderee));
         shipField.setAccessible(true);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
         LOGGER.error("newInstanceError", e);
      }
      return u;
   }
}
