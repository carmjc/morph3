package net.carmgate.morph.ui.inputs;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;

import org.lwjgl.input.Mouse;

/**
 * Allows mouse manipulation in world coordinates.
 */
@Singleton
public class GameMouse {

   @Inject
   private UIContext uiContext;
   private Vector2f posInWorld = new Vector2f();

   public Vector2f getPosInWord() {
      float zoomFactor = uiContext.getViewport().getZoomFactor();
      Vector2f focalPoint = uiContext.getViewport().getFocalPoint();

      int xInWorld = (int) ((getX() - uiContext.getWindow().getWidth() / 2 + focalPoint.x) / zoomFactor);
      int yInWorld = (int) ((-getY() + uiContext.getWindow().getHeight() / 2 + focalPoint.y) / zoomFactor);

      return posInWorld.copy(xInWorld, yInWorld);
   }

   /**
    * @return mouse X position in window coordinates.
    */
   public int getX() {
      return Mouse.getX();
   }

   /**
    * @return mouse Y position in window coordinates.
    */
   public int getY() {
      return Mouse.getY();
   }
}
