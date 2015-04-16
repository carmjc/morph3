package net.carmgate.morph.model.renderers;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.renderers.events.NewRendererFound;

import org.jboss.weld.environment.se.events.ContainerInitialized;

@Singleton
public class ShipSelectRenderer implements Renderer<Ship> {

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent, Event<NewRendererFound> newRendererEventMgr) {
      newRendererEventMgr.fire(new NewRendererFound(this));
   }

   @Override
   public void init() {
      // TODO Auto-generated method stub

   }

   @Override
   public void render(Ship ship) {
      // TODO Auto-generated method stub

   }

}
