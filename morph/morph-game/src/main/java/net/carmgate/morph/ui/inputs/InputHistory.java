package net.carmgate.morph.ui.inputs;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.inputs.UIEvent.HardwareType;

@Singleton
public class InputHistory {
	public static class KeyDown implements Predicate<UIEvent> {
		private int button;

		public KeyDown(int button) {
			this.button = button;
		}

		@Override
		public boolean test(UIEvent t) {
			return t != null && t.getEventType() == EventType.KEYBOARD_DOWN && t.getButton() == button;
		}

	}

	public static class SameMouseEvent implements Predicate<UIEvent> {
		private int button;
		private EventType eventType;

		public SameMouseEvent(EventType eventType, int button) {
			this.eventType = eventType;
			this.button = button;
		}

		@Override
		public boolean test(UIEvent t) {
			return t != null && t.getButton() == button && t.getEventType() == eventType;
		}

	}

	private static final int STACK_SIZE = 100;

	@Inject private Logger LOGGER;
	private final Deque<UIEvent> stack = new LinkedList<>();

	public void addEvent(UIEvent event) {
		stack.addFirst(event);

		// Specifically remove double Mouse.MOVE
		if (getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE && getLastMouseEvent(1).getEventType() == EventType.MOUSE_MOVE) {
			consumeEvents(getLastMouseEvent());
		}

		if (stack.size() > STACK_SIZE) {
			stack.removeLast();
		}
	}

	public void consumeEvents(UIEvent... events) {
		for (UIEvent event : events) {
			stack.remove(event);
			if (event.getEventType() == EventType.KEYBOARD_UP) {
				LOGGER.debug("" + event);
				stack.remove(getLastMatchingEvent(new KeyDown(event.getButton())));
			}
		}

		// if (getLastMouseEvent().getEventType() == EventType.MOUSE_MOVE && getLastMouseEvent(1).getEventType() == EventType.MOUSE_MOVE) {
		// stack.remove(getLastMouseEvent().getEventType());
		// }
	}

	public UIEvent getLastEvent(HardwareType type) {
		try {
			UIEvent first = UIEvent.NOOP;
			for (final UIEvent event : stack) {
				if (event.getEventType().getHardwareType() == type) {
					return event;
				}
			}
			return first;
		} catch (NoSuchElementException e) {
			return UIEvent.NOOP;
		}
	}

	public UIEvent getLastEvent(HardwareType type, int n) {
		int i = 0;
		for (final UIEvent event : stack) {
			if (i++ >= n && event.getEventType().getHardwareType() == type) {
				return event;
			}
		}
		return UIEvent.NOOP;
	}

	public UIEvent getLastKeyboardEvent() {
		return getLastEvent(HardwareType.KEYBOARD);
	}

	public UIEvent getLastKeyboardEvent(int n) {
		return getLastEvent(HardwareType.KEYBOARD, n);
	}

	public UIEvent getLastMatchingEvent(Predicate<UIEvent> predicate) {
		try {
			UIEvent first = UIEvent.NOOP;
			for (final UIEvent event : stack) {
				if (predicate.test(event)) {
					return event;
				}
			}
			return first;
		} catch (NoSuchElementException e) {
			return UIEvent.NOOP;
		}

	}

	public UIEvent getLastMouseEvent() {
		return getLastEvent(HardwareType.MOUSE);
	}

	public UIEvent getLastMouseEvent(int n) {
		return getLastEvent(HardwareType.MOUSE, n);
	}

	public Deque<UIEvent> getStack() {
		return stack;
	}

	public int size() {
		return stack.size();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Input History:\n");
		for (UIEvent e : stack) {
			sb.append("  " + e.getEventType().name() + "[" + e.getButton() + "]");
			if (e.getEventType() == EventType.MOUSE_MOVE) {
				sb.append(" " + e.getPositionInWindow()[0] + "x" + e.getPositionInWindow()[1]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
