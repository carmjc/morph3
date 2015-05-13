package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

public class Zoom implements MouseListener {

   @Inject private Logger LOGGER;
   @Inject private MouseManager mouseManager;
   @Inject private InputHistory inputHistory;
   @Inject private UIContext uiContext;
   @Inject private GameMouse gameMouse;
   @Inject private Conf conf;

   private float ZOOM_VARIATION;
   private float ZOOM_MAX;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      mouseManager.addMouseListener(this);
      ZOOM_VARIATION = conf.getFloatProperty("zoom.variationFactor"); //$NON-NLS-1$
      ZOOM_MAX = conf.getFloatProperty("zoom.max"); //$NON-NLS-1$
   }

   @Override
   public void onMouseEvent() {
      while (inputHistory.getLastMouseEvent().getButton() == Keyboard.KEY_UP
            && inputHistory.getLastMouseEvent().getEventType() == EventType.KEYBOARD_UP
            || inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_WHEEL
            && inputHistory.getLastMouseEvent().getButton() > 0) {

         ViewPort viewport = uiContext.getViewport();

         // Correct max zoom level
         float zoomVariation = ZOOM_VARIATION;
         if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
            zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
         }

         Vector2f fromWindowCenterToMouse = new Vector2f(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
               -uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
         uiContext.getViewport().getFocalPoint().sub(new Vector2f(fromWindowCenterToMouse).scale(1f / viewport.getZoomFactor()))
         .add(new Vector2f(fromWindowCenterToMouse).scale(1f / (viewport.getZoomFactor() * zoomVariation)));
         viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);

         inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
      }

      while (inputHistory.getLastMouseEvent().getButton() == Keyboard.KEY_DOWN
            && inputHistory.getLastMouseEvent().getEventType() == EventType.KEYBOARD_UP
            || inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_WHEEL
            && inputHistory.getLastMouseEvent().getButton() < 0) {

         ViewPort viewport = uiContext.getViewport();

         // Correct max zoom level
         float zoomVariation = 1 / ZOOM_VARIATION;
         if (viewport.getZoomFactor() * zoomVariation > ZOOM_MAX) {
            zoomVariation = ZOOM_MAX / viewport.getZoomFactor();
         }

         Vector2f fromWindowCenterToMouse = new Vector2f(uiContext.getWindow().getWidth() / 2 - gameMouse.getX(),
               -uiContext.getWindow().getHeight() / 2 + gameMouse.getY());
         uiContext.getViewport().getFocalPoint().sub(new Vector2f(fromWindowCenterToMouse).scale(1f / viewport.getZoomFactor()))
               .add(new Vector2f(fromWindowCenterToMouse).scale(1f / (viewport.getZoomFactor() * zoomVariation)));
         viewport.setZoomFactor(viewport.getZoomFactor() * zoomVariation);

         inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
      }
   }
}
