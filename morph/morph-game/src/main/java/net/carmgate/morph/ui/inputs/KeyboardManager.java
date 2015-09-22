package net.carmgate.morph.ui.inputs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;

import net.carmgate.morph.ui.inputs.UIEvent.EventType;

@Singleton
public class KeyboardManager {

	@Inject private Logger LOGGER;
	@Inject private InputHistory inputHistory;

	private List<KeyboardListener> keyboardListeners = new ArrayList<>();

	public void addKeyboardListener(KeyboardListener listener) {
		keyboardListeners.add(listener);
	}

	public void handleKeyboardEvent() {
		while (Keyboard.next()) {
			EventType type;
			if (Keyboard.getEventKeyState()) {
				type = EventType.KEYBOARD_DOWN;
				UIEvent uiEvent = new UIEvent(type, Keyboard.getEventCharacter(), Keyboard.getEventKey());
				inputHistory.addEvent(uiEvent);
			} else {
				type = EventType.KEYBOARD_UP;
				int button = -1;
				int key = -1;
				for (UIEvent event : inputHistory.getStack()) {
					if (event.getEventType() == EventType.KEYBOARD_DOWN) {
						if (!Keyboard.isKeyDown(event.getKey())) {
							button = event.getButton();
							key = event.getKey();
							break;
						}
					}
				}
				UIEvent uiEvent = new UIEvent(type, button, key);
				inputHistory.addEvent(uiEvent);
			}

			keyboardListeners.forEach(kl -> {
				kl.onKeyboardEvent();
			});
		}
	}

	public void removeKeyboardListener(KeyboardListener listener) {
		keyboardListeners.remove(listener);
	}
}
