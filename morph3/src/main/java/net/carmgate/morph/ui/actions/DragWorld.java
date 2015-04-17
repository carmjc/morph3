package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.common.InteractionStack;
import net.carmgate.morph.ui.inputs.common.MouseListener;
import net.carmgate.morph.ui.inputs.common.UIEvent.EventType;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;

@Singleton
public class DragWorld implements MouseListener {

   @Inject
   private Logger LOGGER;
   @Inject
   private MouseManager mouseManager;
   @Inject
   private InteractionStack inputHistory;
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
      if (inputHistory.getLastEvents(2).get(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastEvents(2).get(1).getButton() == 0
            && inputHistory.getLastEvents(2).get(0).getEventType() == EventType.MOUSE_MOVE) {

         // Model.getModel().getViewport().setLockedOnEntity(null);

         dragContext.setOldFP(uiContext.getViewport().getFocalPoint());
         dragContext.setOldMousePosInWindow(gameMouse.getX(), gameMouse.getY());

         final Vector2f oldFP = dragContext.getOldFP();
         final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
         if (oldFP != null) {
            final ViewPort viewport = uiContext.getViewport();
            final Vector2f fp = viewport.getFocalPoint();
            fp.x = oldFP.x - (Mouse.getX() - oldMousePosInWindow.x);
            fp.y = oldFP.y + (Mouse.getY() - oldMousePosInWindow.y);
         }
         inputHistory.consumeLastEvents(2);
      }
      while (inputHistory.getLastEvents(1).get(0).getEventType() == EventType.MOUSE_MOVE
            && dragContext.dragInProgress()) {

         final Vector2f oldFP = dragContext.getOldFP();
         final Vector2f oldMousePosInWindow = dragContext.getOldMousePosInWindow();
         if (oldFP != null) {
            final ViewPort viewport = uiContext.getViewport();
            final Vector2f fp = viewport.getFocalPoint();
            fp.x = oldFP.x - (Mouse.getX() - oldMousePosInWindow.x);
            fp.y = oldFP.y + (Mouse.getY() - oldMousePosInWindow.y);
         }

         inputHistory.consumeLastEvents(1);
      }
      if (inputHistory.getLastEvents(1).get(0).getEventType() == EventType.MOUSE_BUTTON_UP
            && dragContext.dragInProgress()) {
         LOGGER.debug("dragContext reset");
         dragContext.reset();
         inputHistory.consumeLastEvents(1);
      }
   }
}