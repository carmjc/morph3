package net.carmgate.morph.ui.inputs;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.model.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.ViewPort;

import org.lwjgl.input.Mouse;

/**
 * Allows mouse manipulation in world coordinates.
 */
@Singleton
public class GameMouse {

   @Inject
   private UIContext uiContext;

   public Vector2f getPosInWord() {
      return new Vector2f(getXInWorld(), getYInWorld());
   }

   /**
    * @return mouse X position in window coordinates.
    */
   public int getX() {
      return Mouse.getX();
   }

   /**
    * @return mouse X position in world coordinates.
    */
   public int getXInWorld() {
      final ViewPort viewport = uiContext.getViewport();
      return (int) ((Mouse.getX() - uiContext.getWindow().getWidth() / 2 + viewport.getFocalPoint().x) / viewport.getZoomFactor());
   }

   /**
    * @return mouse Y position in window coordinates.
    */
   public int getY() {
      return Mouse.getY();
   }

   /**
    * @return mouse Y position in world coordinates.
    */
   public int getYInWorld() {
      final ViewPort viewport = uiContext.getViewport();
      return (int) ((-Mouse.getY() + uiContext.getWindow().getHeight() / 2 + viewport.getFocalPoint().y) / viewport.getZoomFactor());
   }
}
