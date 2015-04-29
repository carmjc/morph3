package net.carmgate.morph.ui.actions;

import java.nio.IntBuffer;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.Window;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.renderers.entities.ship.ShipSelectRenderer;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.slf4j.Logger;

public class Select implements MouseListener {

   @Inject private Logger LOGGER;
   @Inject private MouseManager mouseManager;
   @Inject private InputHistory inputHistory;
   @Inject private UIContext uiContext;
   @Inject private World world;
   @Inject private ShipSelectRenderer shipSelectRenderer;

   public Select() {
      // TODO Auto-generated constructor stub
   }

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      mouseManager.addMouseListener(this);
   }

   @Override
   public void onMouseEvent() {
      if (inputHistory.getLastMouseEvent(1).getButton() == 0 && inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastMouseEvent(0).getButton() == 0 && inputHistory.getLastMouseEvent(0).getEventType() == EventType.MOUSE_BUTTON_UP) {
         LOGGER.debug("click detected");
         select(Mouse.getX() - uiContext.getWindow().getWidth() / 2, Mouse.getY() - uiContext.getWindow().getHeight() / 2);
         inputHistory.consumeEvents(inputHistory.getLastMouseEvent(0), inputHistory.getLastMouseEvent(1));
      }
   }

   /**
    * Renders the scene for selection.
    * Can also be used directly for debugging purposes to show the pickable areas.
    *
    * @param zoomFactor
    * @param glMode
    */
   public void render(int glMode) {

      Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
      float zoomFactor = uiContext.getViewport().getZoomFactor();
      GL11.glScalef(zoomFactor, zoomFactor, 1);
      GL11.glTranslatef(-focalPoint.x, -focalPoint.y, -0);

      // In select mode, we render the model elements in reverse order, because, the first items drawn will
      // be the first picked
      for (Ship ship : world.getShips()) {
         GL11.glPushName(ship.getId());
         final Vector2f pos = ship.getPos();
         GL11.glTranslatef(pos.x, pos.y, 0);
         shipSelectRenderer.render(ship);
         GL11.glTranslatef(-pos.x, -pos.y, 0);
         GL11.glPopName();
      }

      GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
      GL11.glScalef(1f / zoomFactor, 1f / zoomFactor, 1);
   }

   /**
    * Picks model elements.
    *
    * @param x
    * @param y
    */
   private void select(int x, int y) {

      LOGGER.debug("Picking at " + x + " " + y);

      // get viewport
      IntBuffer viewport = BufferUtils.createIntBuffer(16);
      GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

      IntBuffer selectBuf = BufferUtils.createIntBuffer(512);
      GL11.glSelectBuffer(selectBuf);
      GL11.glRenderMode(GL11.GL_SELECT);

      GL11.glInitNames();

      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();

      GLU.gluPickMatrix(x, y, 6.0f, 6.0f, viewport);

      Window window = uiContext.getWindow();
      GL11.glOrtho(0, window.getWidth(), 0, -window.getHeight(), 1, -1);
      GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

      render(GL11.GL_SELECT);

      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glPopMatrix();
      GL11.glFlush();

      int hits = GL11.glRenderMode(GL11.GL_RENDER);

      // For debugging purpose only ...
      // This allows to see the select buffer
      String result = "[";
      for (int i = 0; i < selectBuf.capacity(); i++)
      {
         result += selectBuf.get(i) + ", ";
      }
      LOGGER.debug("hits: " + hits + ", result : " + result + "]");

      // Get the model elements picked
      // The current index we are looking for in the select buffer
      int selectBufIndex = 0;

      // The picked entity if any
      Ship pickedShip = null;

      // Iterate over the hits
      for (int i = 0; i < hits; i++) {
         // get the number of names on this part of the stack
         // int nbNames = selectBuf.get(selectBufIndex++);

         // jump over the two extremes of the picking z-index range
         selectBufIndex += 3;

         // get the matching element in the model
         int shipId = selectBuf.get(selectBufIndex);
         for (Ship ship : world.getShips()) {
            if (ship.getId() == shipId) {
               pickedShip = ship;
            }
         }

      }

      if (pickedShip != null) {
         uiContext.setSelectedShip(pickedShip);
      }
   }

}
