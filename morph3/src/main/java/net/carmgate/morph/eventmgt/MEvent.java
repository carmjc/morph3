package net.carmgate.morph.eventmgt;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MEvent<T> {

   @Inject
   private MEventManager eventManager;

   public void fire(T event) {
      eventManager.setFiringEvent(true);
      eventManager.getObservingMethodsMapByEvent().get(event.getClass()).forEach(method -> {
         eventManager.getInstances().get(method.getClass()).forEach(object -> {
            try {
               method.invoke(object, event);
            } catch (Exception e) {
               throw new EventManagementException(e);
            }
         });
      });
      eventManager.setFiringEvent(false);

      eventManager.getTmp().forEach(o -> {
         eventManager.scanAndRegister(o);
      });
   }
}
