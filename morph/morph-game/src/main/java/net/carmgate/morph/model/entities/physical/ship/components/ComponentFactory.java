package net.carmgate.morph.model.entities.physical.ship.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.events.mgt.MEventManager;

@Singleton
public class ComponentFactory {

   @Inject private Instance<Component> components;
   @Inject private MEventManager eventManager;

   public <U extends Component> U newInstance(Class<U> clazz) {
      final U u = components.select(clazz).get();
      eventManager.scanAndRegister(u);
      return u;
   }

}