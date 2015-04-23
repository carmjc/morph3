package net.carmgate.morph.model.entities.physical;

import java.util.HashSet;
import java.util.Set;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.api.Renderable;

public abstract class PhysicalEntity implements Renderable {

   private final Vector2f pos = new Vector2f();
   private final Vector2f speed = new Vector2f();
   private final Vector2f accel = new Vector2f();
   private float rotate = 0f;
   private float rotateSpeed = 0f;
   private final Set<ForceSource> forceSources = new HashSet<>();
   protected float mass;

   public final Vector2f getAccel() {
      return accel;
   }

   public final Vector2f getPos() {
      return pos;
   }

   public final Vector2f getSpeed() {
      return speed;
   }

   public final Set<ForceSource> getForceSources() {
      return forceSources;
   }

   public final float getMass() {
      return mass;
   }

   public void setMass(float mass) {
      this.mass = mass;
   }

   public float getRotate() {
      return rotate;
   }

   public void setRotate(float rotate) {
      this.rotate = rotate;
   }

   public float getRotateSpeed() {
      return rotateSpeed;
   }

   public void setRotateSpeed(float rotateSpeed) {
      this.rotateSpeed = rotateSpeed;
   }

}
