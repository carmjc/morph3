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
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.SelectRenderer.TargetType;
import net.carmgate.morph.ui.renderers.entities.ship.ShipSelectRenderer;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetContainer;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;

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
      if (inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastMouseEvent().getButton() == 0) {
         LOGGER.debug("click detected"); //$NON-NLS-1$
         Ship selectedShip = uiContext.getSelectedShip();
         select(Mouse.getX() - uiContext.getWindow().getWidth() / 2, Mouse.getY() - uiContext.getWindow().getHeight() / 2);
         if (selectedShip != null && uiContext.getSelectedShip() == null) {
            uiContext.setSelectedShip(selectedShip);
         }
      }
      if (inputHistory.getLastMouseEvent(1).getButton() == 0 && inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastMouseEvent(1).getButton() == 0
            && inputHistory.getLastMouseEvent().getButton() == 0 && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP) {
         LOGGER.debug("click detected"); //$NON-NLS-1$
         select(Mouse.getX() - uiContext.getWindow().getWidth() / 2, Mouse.getY() - uiContext.getWindow().getHeight() / 2);
      }
   }

   /**
    * Renders the scene for selection.
    * Can also be used directly for debugging purposes to show the pickable areas.
    *
    * @param zoomFactor
    * @param glMode
    */
   public void render() {

      Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
      float zoomFactor = uiContext.getViewport().getZoomFactor();

      float x = -uiContext.getWindow().getWidth() / 2;
      float y = -uiContext.getWindow().getHeight() / 2;
      GL11.glTranslatef(x, y, 0);
      renderWidgetForSelect(uiContext.getWidgetRoot());
      GL11.glTranslatef(-x, -y, 0);

      GL11.glScalef(zoomFactor, zoomFactor, 1);
      GL11.glTranslatef(-focalPoint.x, -focalPoint.y, -0);

      for (Ship ship : world.getShips()) {
         GL11.glPushName(SelectRenderer.TargetType.PHYSICAL_ENTITY.ordinal());
         GL11.glPushName(ship.getId());
         final Vector2f pos = ship.getPos();
         GL11.glTranslatef(pos.x, pos.y, 0);
         shipSelectRenderer.render(ship);
         GL11.glTranslatef(-pos.x, -pos.y, 0);
         GL11.glPopName();
         GL11.glPopName();
      }

      GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
      GL11.glScalef(1f / zoomFactor, 1f / zoomFactor, 1);
   }

   private void renderWidgetForSelect(Widget widget) {
      if (widget instanceof WidgetContainer) {
         for (Widget childWidget : ((WidgetContainer) widget).getWidgets()) {
            GL11.glTranslatef(childWidget.getPosition()[0], childWidget.getPosition()[1], 0);
            renderWidgetForSelect(childWidget);
            GL11.glTranslatef(-childWidget.getPosition()[0], -childWidget.getPosition()[1], 0);
         }
      }

      if (widget instanceof WidgetMouseListener) {
         GL11.glPushName(SelectRenderer.TargetType.WIDGET.ordinal());
         GL11.glPushName(widget.getId());
         ((WidgetMouseListener) widget).renderInteractiveAreas();
         GL11.glPopName();
         GL11.glPopName();
      }
   }

   /**
    * Picks model elements.
    *
    * @param x
    * @param y
    * @param b
    */
   public TargetType select(int x, int y) {

      LOGGER.debug("Picking at " + x + " " + y); //$NON-NLS-1$ //$NON-NLS-2$
      TargetType targetType = null;

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

      render();

      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glPopMatrix();
      GL11.glFlush();

      int hits = GL11.glRenderMode(GL11.GL_RENDER);

      // For debugging purpose only ...
      // This allows to see the select buffer
      String result = "["; //$NON-NLS-1$
      for (int i = 0; i < selectBuf.capacity(); i++)
      {
         result += selectBuf.get(i) + ", "; //$NON-NLS-1$
      }
      LOGGER.debug("hits: " + hits + ", result : " + result + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      // Get the model elements picked
      // The current index we are looking for in the select buffer
      int selectBufIndex = 0;

      // The picked entity if any
      Ship pickedShip = null;
      Widget pickedWidget = null;

      // Iterate over the hits
      for (int i = 0; i < hits; i++) {
         // get the number of names on this part of the stack
         // int nbNames = selectBuf.get(selectBufIndex++);

         // jump over the two extremes of the picking z-index range
         selectBufIndex += 3;

         int targetTypeId = selectBuf.get(selectBufIndex++);
         if (targetTypeId == SelectRenderer.TargetType.WIDGET.ordinal()) {
            pickedWidget = uiContext.getWidgets().get(selectBuf.get(selectBufIndex++));
            targetType = TargetType.WIDGET;
            LOGGER.debug("Widget selected");
         }
         if (targetTypeId == SelectRenderer.TargetType.PHYSICAL_ENTITY.ordinal()) {
            // get the matching element in the model
            int shipId = selectBuf.get(selectBufIndex++);
            for (Ship ship : world.getShips()) { // FIXME We should implement the same logic as for widgets with a big map
               if (ship.getId() == shipId) {
                  pickedShip = ship;
               }
            }
            targetType = TargetType.PHYSICAL_ENTITY;
         }
      }

      uiContext.setSelectedWidget(pickedWidget);
      if (pickedWidget == null) {
         uiContext.setSelectedShip(pickedShip);
      }

      return targetType;
   }

}
