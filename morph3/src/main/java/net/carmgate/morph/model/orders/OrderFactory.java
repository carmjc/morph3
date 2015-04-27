package net.carmgate.morph.model.orders;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.eventmgt.MEventManager;
import net.carmgate.morph.model.entities.physical.ship.Ship;

@Singleton
public class OrderFactory {

   @Inject private Instance<Order> orders;
   @Inject private MEventManager eventManager;

   @SuppressWarnings("unchecked")
   public <U extends Order> U newInstance(OrderType orderType, Ship orderee) {
      final U u = (U) orders.select(orderType.getClazz()).get();
      eventManager.scanAndRegister(u);
      u.setOrderee(orderee);
      return u;
   }
}
