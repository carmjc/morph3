package net.carmgate.morph.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.geometry.Vector2f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the properties of the view through which the world is seen.
 * The viewport is rectangular in shape.
 */
public class ViewPort {

   @Inject private Conf conf;

   @SuppressWarnings("unused")
   private static final Logger LOGGER = LoggerFactory.getLogger(ViewPort.class);

   /** The intersection of the diagonals of the viewport (in world coordinates). */
   private final Vector2f focalPoint = new Vector2f();

   /** The rotation of the scene around the focal point (in radians). */
   private final float rotation = 0;

   /** The zoom factor. > 1 means what you see is bigger. */
   private float zoomFactor;

   @PostConstruct
   private void init() {
      zoomFactor = conf.getFloatProperty("ui.viewPort.zoomFactor");
   }

   // private Entity lockedOnEntity;

   /** The intersection of the diagonals of the viewport (in <b>world coordinates</b>). */
   public Vector2f getFocalPoint() {
      return focalPoint;
   }

   // public Entity getLockedOnEntity() {
   // return lockedOnEntity;
   // }

   /** The rotation of the scene around the focal point. */
   public float getRotation() {
      return rotation;
   }

   /** The zoom factor. > 1 means what you see is bigger. */
   public float getZoomFactor() {
      return zoomFactor;
   }

   // public void setLockedOnEntity(Entity lockedOnEntity) {
   // this.lockedOnEntity = lockedOnEntity;
   // }

   public void setZoomFactor(float zoomFactor) {
      this.zoomFactor = zoomFactor;
   }

}
