package net.carmgate.morph.events;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.mgt.MEventManager;

@Singleton
public class WorldEventFactory {

   @Inject
   Instance<WorldEvent> worldEventInstances;

   @Inject
   private MEventManager eventManager;

   @SuppressWarnings("unchecked")
   public <U extends WorldEvent> U newInstance(WorldEventType type) {
      U u = (U) worldEventInstances.select(type.getClazz()).get();
      eventManager.scanAndRegister(u);
      return u;
   }

}
