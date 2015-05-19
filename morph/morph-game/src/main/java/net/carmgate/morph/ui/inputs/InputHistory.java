package net.carmgate.morph.ui.inputs;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import net.carmgate.morph.ui.inputs.UIEvent.EventType;
import net.carmgate.morph.ui.inputs.UIEvent.HardwareType;

@Singleton
public class InputHistory {
   private static final int STACK_SIZE = 100;
   private final Deque<UIEvent> stack = new LinkedList<>();

   public void addEvent(UIEvent event) {
      stack.addFirst(event);
      if (stack.size() > STACK_SIZE) {
         stack.removeLast();
      }
   }

   public void consumeEvents(UIEvent... events) {
      for (UIEvent event : events) {
         stack.remove(event);
      }
   }

   public UIEvent getLastMouseEvent() {
      return getLastEvent(HardwareType.MOUSE);
   }

   public UIEvent getLastKeyboardEvent() {
      return getLastEvent(HardwareType.KEYBOARD);
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

   public UIEvent getLastMouseEvent(int n) {
      return getLastEvent(HardwareType.MOUSE, n);
   }

   public UIEvent getLastKeyboardEvent(int n) {
      return getLastEvent(HardwareType.KEYBOARD, n);
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

   public int size() {
      return stack.size();
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("inputHistory:\n");
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
