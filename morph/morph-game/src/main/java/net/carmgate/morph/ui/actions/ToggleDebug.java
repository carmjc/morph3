package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.renderers.RenderMode;

import org.jboss.weld.environment.se.events.ContainerInitialized;

public class ToggleDebug implements KeyboardListener {

   @Inject private KeyboardManager keyboardManager;
   @Inject private InputHistory inputHistory;
   @Inject private UIContext uiContext;
   @Inject private Conf conf;

   @SuppressWarnings("unused")
   private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      keyboardManager.addKeyboardListener(this);
   }

   @Override
   public void onKeyboardEvent() {
      if (inputHistory.getLastKeyboardEvent().getButton() == conf.getCharProperty("action.toggleDebug.key")) { //$NON-NLS-1$
         uiContext.setRenderMode(uiContext.getRenderMode() == RenderMode.DEBUG ? RenderMode.NORMAL : RenderMode.DEBUG);
         inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
      }
      if (inputHistory.getLastKeyboardEvent().getButton() == conf.getCharProperty("action.toggleSelectDebug.key") //$NON-NLS-1$
            && (uiContext.getRenderMode() == RenderMode.DEBUG || uiContext.getRenderMode() == RenderMode.SELECT_DEBUG)) {
         uiContext.setRenderMode(uiContext.getRenderMode() == RenderMode.SELECT_DEBUG ? RenderMode.DEBUG : RenderMode.SELECT_DEBUG);
         inputHistory.consumeEvents(inputHistory.getLastKeyboardEvent());
      }
   }

}
