package net.carmgate.morph.ui.widgets;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.carmgate.morph.events.mgt.MEventManager;

public class WidgetFactory {

   @Inject private Instance<Widget> widgets;
   @Inject private MEventManager eventManager;

   private int idGen = 0;

   public <U extends Widget> U newInstance(Class<U> widget) {
      final U u = widgets.select(widget).get();
      u.setId(idGen++);
      eventManager.scanAndRegister(u);
      return u;
   }

}
