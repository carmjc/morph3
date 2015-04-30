package net.carmgate.morph.model.events.entities.ship;

import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.events.WorldEvent;

public class PhysicalEntityToBeRemoved implements WorldEvent {

   private PhysicalEntity entity;

   public PhysicalEntity getEntity() {
      return entity;
   }

   public void setEntity(PhysicalEntity entity) {
      this.entity = entity;
   }
}
