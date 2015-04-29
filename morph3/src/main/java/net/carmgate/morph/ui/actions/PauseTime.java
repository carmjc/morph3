package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.renderers.RenderMode;

import org.jboss.weld.environment.se.events.ContainerInitialized;

public class PauseTime implements KeyboardListener {

   @Inject private KeyboardManager keyboardManager;
   @Inject private InputHistory inputHistory;
   @Inject private World world;
   @Inject private UIContext uiContext;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      keyboardManager.addKeyboardListener(this);
   }

   @Override
   public void onKeyboardEvent() {
      if (inputHistory.getLastKeyboardEvent().getButton() == ' ') {
         world.toggleTimeFrozen();
         inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
      }
      if (inputHistory.getLastKeyboardEvent().getButton() == 'd') {
         uiContext.setRenderMode(uiContext.getRenderMode() == RenderMode.DEBUG ? RenderMode.NORMAL : RenderMode.DEBUG);
         inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
      }
   }

}
