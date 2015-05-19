package net.carmgate.morph.ui.actions;

import javax.inject.Inject;

import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.MouseListener;
import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetMouseListener;

public class DragInWidget implements MouseListener {

   @Inject private InputHistory inputHistory;
   @Inject private DragContext dragContext;
   @Inject private UIContext uiContext;

   @Override
   public void onMouseEvent() {
      Widget widget = uiContext.getSelectedWidget();
      if (widget == null) {
         return;
      }

      if (inputHistory.getLastMouseEvent(1).getEventType() == EventType.MOUSE_BUTTON_DOWN
            && inputHistory.getLastMouseEvent(1).getButton() == 0
            && inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE) {

         ((WidgetMouseListener) widget).onDragStart();

         inputHistory.consumeEvents(inputHistory.getLastMouseEvent(), inputHistory.getLastMouseEvent(1));
      }
      while (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE
            && dragContext.dragInProgress()) {

         ((WidgetMouseListener) widget).onDragContinue();

         inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
      }
      if (inputHistory.getLastMouseEvent().getEventType() == EventType.MOUSE_BUTTON_UP
            && dragContext.dragInProgress()) {

         ((WidgetMouseListener) widget).onDragStop();

         dragContext.reset();
         inputHistory.consumeEvents(inputHistory.getLastMouseEvent());
      }
   }

}
