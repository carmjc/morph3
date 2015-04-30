package net.carmgate.morph.ui.inputs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.ui.inputs.UIEvent.EventType;

import org.lwjgl.input.Keyboard;

@Singleton
public class KeyboardManager {
   @Inject private InputHistory inputHistory;

   private List<KeyboardListener> keyboardListeners = new ArrayList<>();

   public void handleKeyboardEvent() {
      while (Keyboard.next()) {
         EventType type;
         if (Keyboard.getEventKeyState()) {
            type = EventType.KEYBOARD_DOWN;
         } else {
            type = EventType.KEYBOARD_UP;
         }
         UIEvent uiEvent = new UIEvent(type, Keyboard.getEventCharacter());
         inputHistory.addEvent(uiEvent);
      }

      keyboardListeners.forEach(kl -> {
         kl.onKeyboardEvent();
      });
   }

   public void addKeyboardListener(KeyboardListener listener) {
      keyboardListeners.add(listener);
   }

   public void removeKeyboardListener(KeyboardListener listener) {
      keyboardListeners.remove(listener);
   }
}
