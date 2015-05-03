package net.carmgate.morph.events.entities.ship;

import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;

public class PhysicalEntityToBeRemoved implements WorldEvent {

   private PhysicalEntity entity;

   public PhysicalEntity getEntity() {
      return entity;
   }

   public void setEntity(PhysicalEntity entity) {
      this.entity = entity;
   }
}
