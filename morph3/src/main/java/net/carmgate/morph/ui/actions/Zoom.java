package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.api.MouseListener;
import net.carmgate.morph.ui.inputs.api.UIEvent.EventType;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

public class Zoom implements MouseListener {

   @Inject
   private Logger LOGGER;
   @Inject
   private MouseManager mouseManager;
   @Inject
   private InputHistory inputHistory;
   @Inject
   private UIContext uiContext;
   @Inject
   private GameMouse gameMouse;
   @Inject
   private Conf conf;

   private float ZOOM_VARIATION;
   private float ZOOM_MAX;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      mouseManager.addMouseListener(this);
      ZOOM_VARIATION = conf.getFloatProperty("zoom.variationFactor");
      ZOOM_MAX = conf.getFloatProperty("zoom.max");
   }

   @Override
   public void onMouseEvent() {
      while (inputHistory.getLastEvent().getButton() == Keyboard.KEY_UP
            && inputHistory.getLastEvent().getEventType() == EventType.KEYBOARD_UP
            || inputHistory.getLastEvent().getEventType() == EventType.MOUSE_WHEEL
            && inputHistory.getLastEvent().getButton() > 0) {

         ViewPort viewport = uiContext.getViewport();

         // Correct max zoom level
         float zoomVariation = ZOOM_VARIATION;
         if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
            zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
         }

         viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);
         Vector2f fromWindowCenterToMouse = new Vector2f(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
               -uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
         uiContext.getViewport().getFocalPoint().add(new Vector2f(fromWindowCenterToMouse).scale(1f / zoomVariation)).scale(zoomVariation)
               .sub(new Vector2f(fromWindowCenterToMouse).scale(zoomVariation));

         inputHistory.consumeLastEvents(1);
      }

      while (inputHistory.getLastEvent().getButton() == Keyboard.KEY_DOWN
            && inputHistory.getLastEvent().getEventType() == EventType.KEYBOARD_UP
            || inputHistory.getLastEvent().getEventType() == EventType.MOUSE_WHEEL
            && inputHistory.getLastEvent().getButton() < 0) {

         ViewPort viewport = uiContext.getViewport();

         // Correct max zoom level
         float zoomVariation = 1 / ZOOM_VARIATION;
         if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
            zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
         }

         viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);
         Vector2f fromWindowCenterToMouse = new Vector2f(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
               -uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
         uiContext.getViewport().getFocalPoint().add(new Vector2f(fromWindowCenterToMouse).scale(1f / zoomVariation)).scale(zoomVariation)
               .sub(new Vector2f(fromWindowCenterToMouse).scale(zoomVariation));

         inputHistory.consumeLastEvents(1);
      }
   }
}
