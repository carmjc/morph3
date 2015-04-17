package net.carmgate.morph.ui.actions;

import javax.inject.Singleton;

import net.carmgate.morph.model.Vector2f;
import net.carmgate.morph.ui.inputs.common.UIEvent;

@Singleton
public class DragContext {
   private Vector2f oldFP;
   private Vector2f oldMousePosInWindow;

   public boolean dragInProgress() {
      return oldFP != null;
   }

   public Vector2f getOldFP() {
      return oldFP;
   }

   public Vector2f getOldMousePosInWindow() {
      return oldMousePosInWindow;
   }

   public boolean hasBeenDragged(UIEvent event) {
      return oldMousePosInWindow != null
            && (event.getPositionInWindow()[0] != oldMousePosInWindow.x || event.getPositionInWindow()[1] != oldMousePosInWindow.y);
   }

   public void reset() {
      oldFP = null;
      oldMousePosInWindow = null;
   }

   public void setOldFP(Vector2f oldFP) {
      if (this.oldFP == null) {
         this.oldFP = new Vector2f(oldFP);
      } else {
         this.oldFP.copy(oldFP);
      }
   }

   public void setOldMousePosInWindow(float x, float y) {
      if (oldMousePosInWindow == null) {
         oldMousePosInWindow = new Vector2f(x, y);
      } else {
         oldMousePosInWindow.copy(x, y);
      }
   }
}
