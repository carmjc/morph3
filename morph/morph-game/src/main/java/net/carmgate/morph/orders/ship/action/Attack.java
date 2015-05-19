package net.carmgate.morph.orders.ship.action;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.events.WorldEventFactory;
import net.carmgate.morph.events.WorldEventType;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.entities.ship.ShipHit;
import net.carmgate.morph.events.mgt.MEvent;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.orders.OrderFactory;
import net.carmgate.morph.orders.OrderType;
import net.carmgate.morph.orders.ship.move.CloseIn;

import org.slf4j.Logger;

public class Attack extends ActionOrder {

   @Inject private MEvent<WorldEvent> worldEventMgr;
   @Inject private WorldEventFactory worldEventFactory;
   @Inject private OrderFactory orderFactory;
   @Inject private Logger LOGGER;
   @Inject private Conf conf;

   private Ship target;
   private final Vector2f tmpVect = new Vector2f();

   @Override
   protected void evaluate() {
      setNextEvalTime(getNextEvalTime() + 1000);

      if (getOrderee().getMoveOrder() == null || getOrderee().getMoveOrder().getParentOrder() != this) {
         final CloseIn closeInOrder = orderFactory.newInstance(OrderType.CLOSE_IN, getOrderee());
         closeInOrder.setDistance(conf.getIntProperty("order.attack.maxDistance") * 0.5f); //$NON-NLS-1$
         closeInOrder.setTarget(target);
         getOrderee().add(closeInOrder);
      }

      // Is the target ship close enough ?
      tmpVect.copy(target.getPos()).sub(getOrderee().getPos());
      final float distance = tmpVect.length();
      if (distance > conf.getIntProperty("order.attack.maxDistance")) { //$NON-NLS-1$
         return;
      }

      Component laser = getOrderee().getComponents().get(ComponentType.LASERS);
      laser.setTarget(target);
      laser.setActive(true);

      // Create the event
      if (!laser.isFamished()) {
         final ShipHit shipHit = worldEventFactory.newInstance(WorldEventType.SHIP_HIT);
         shipHit.init(getOrderee(), target, 1 * getOrderee().getComponentsComposition().get(getComponentTypes()[0])); // FIXME can there be more than one component type ?
         worldEventMgr.fire(shipHit);
      }
   }

   protected void onDeadShip(@MObserves ShipDeath deadShip) {
      if (deadShip.getShip() == target) {
         Component laser = getOrderee().getComponents().get(ComponentType.LASERS);
         laser.setActive(false);
         setDone(true);
      }
   }

   public void setTarget(Ship target) {
      this.target = target;
   }

   @Override
   // FIXME transform this into an Annotation
   public ComponentType[] getComponentTypes() {
      return new ComponentType[] { ComponentType.LASERS };
   }

   @Override
   public int getCriticity() {
      return 50;
   }

   @Override
   public void onRemoveOrder() {
      Component laser = getOrderee().getComponents().get(ComponentType.LASERS);
      laser.setActive(false);
      setDone(true);
   }

}
