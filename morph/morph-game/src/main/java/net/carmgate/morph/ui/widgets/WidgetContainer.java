package net.carmgate.morph.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class WidgetContainer extends Widget {

   private List<Widget> widgets = new ArrayList<>();

   @Override
   public void renderWidget() {
      for (Widget widget : widgets) {
         GL11.glTranslatef(widget.getPosition()[0], widget.getPosition()[1], 0);
         widget.renderWidget();
         GL11.glTranslatef(-widget.getPosition()[0], -widget.getPosition()[1], 0);
      }
   }

   public boolean add(Widget e) {
      return widgets.add(e);
   }

   public List<Widget> getWidgets() {
      return widgets;
   }

}
