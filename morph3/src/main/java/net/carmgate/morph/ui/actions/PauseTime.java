package net.carmgate.morph.ui.actions;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardListener;
import net.carmgate.morph.ui.inputs.KeyboardManager;

import org.jboss.weld.environment.se.events.ContainerInitialized;

public class PauseTime implements KeyboardListener {

   @Inject private KeyboardManager keyboardManager;
   @Inject private InputHistory inputHistory;
   @Inject private World world;

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
   }

}
