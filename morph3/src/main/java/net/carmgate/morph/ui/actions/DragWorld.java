package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.api.MouseListener;
import net.carmgate.morph.ui.inputs.api.UIEvent.EventType;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

@Singleton
public class DragWorld implements MouseListener {

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
   private DragContext dragContext;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      mouseManager.addMouseListener(this);
   }

   @Override
   public void onMouseEvent() {
      if (inputHistory.getLastEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastEvent(1).getButton() == 0
            && inputHistory.getLastEvent().getEventType() == EventType.MOUSE_MOVE) {

         // Model.getModel().getViewport().setLockedOnEntity(null);

         dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
         dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());

         final Vector2f oldFP = dragContext.getOldFP();
         final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
         if (oldFP != null) {
            final ViewPort viewport = uiContext.getViewport();
            final Vector2f fp = viewport.getFocalPoint();
            fp.x = (oldFP.x * uiContext.getViewport().getZoomFactor() - (gameMouse.getX() - oldMousePosInWindow.x)) / uiContext.getViewport().getZoomFactor();
            fp.y = (oldFP.y * uiContext.getViewport().getZoomFactor() + (gameMouse.getY() - oldMousePosInWindow.y)) / uiContext.getViewport().getZoomFactor();
         }
         inputHistory.consumeLastEvents(2);
      }
      while (inputHistory.getLastEvent().getEventType() == EventType.MOUSE_MOVE
            && dragContext.dragInProgress()) {

         final Vector2f oldFP = dragContext.getOldFP();
         final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
         if (oldFP != null) {
            final ViewPort viewport = uiContext.getViewport();
            final Vector2f fp = viewport.getFocalPoint();
            fp.x = (oldFP.x * uiContext.getViewport().getZoomFactor() - (gameMouse.getX() - oldMousePosInWindow.x)) / uiContext.getViewport().getZoomFactor();
            fp.y = (oldFP.y * uiContext.getViewport().getZoomFactor() + (gameMouse.getY() - oldMousePosInWindow.y)) / uiContext.getViewport().getZoomFactor();
         }

         inputHistory.consumeLastEvents(1);
      }
      if (inputHistory.getLastEvent().getEventType() == EventType.MOUSE_BUTTON_UP
            && dragContext.dragInProgress()) {
         LOGGER.debug("dragContext reset");
         dragContext.reset();
         inputHistory.consumeLastEvents(1);
      }
   }
}
