package net.carmgate.morph.ui.inputs;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import net.carmgate.morph.ui.inputs.api.UIEvent;

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

   public void consumeLastEvents(int nb) {
      for (int i = 0; i < nb; i++) {
         stack.removeFirst();
      }
   }

   public UIEvent getLastEvent() {
      try {
         return stack.getFirst();
      } catch (NoSuchElementException e) {
         return UIEvent.NOOP;
      }
   }

   public UIEvent getLastEvent(int n) {
      int i = 0;
      for (final UIEvent event : stack) {
         if (i++ >= n) {
            return event;
         }
      }
      return UIEvent.NOOP;
   }

   public int size() {
      return stack.size();
   }
}
