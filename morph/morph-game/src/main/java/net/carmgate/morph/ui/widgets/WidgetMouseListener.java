package net.carmgate.morph.ui.widgets;

public interface WidgetMouseListener {

   void renderInteractiveAreas();

   void onDragStart();

   void onDragContinue();

   void onDragStop();

}
