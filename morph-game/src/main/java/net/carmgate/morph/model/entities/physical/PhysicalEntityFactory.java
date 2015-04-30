package net.carmgate.morph.model.entities.physical;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.eventmgt.MEventManager;

@Singleton
public class PhysicalEntityFactory {

   @Inject private Instance<PhysicalEntity> physicalEntities;
   @Inject private MEventManager eventManager;

   private int idGen = 0;

   @SuppressWarnings("unchecked")
   public <U extends PhysicalEntity> U newInstance(PhysicalEntityType type) {
      final U u = (U) physicalEntities.select(type.getClazz()).get();
      u.setId(idGen++);
      eventManager.scanAndRegister(u);
      return u;
   }

}
