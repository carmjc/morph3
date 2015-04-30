package net.carmgate.morph.model.geometry;

@SuppressWarnings("serial")
public class Vector2f extends org.lwjgl.util.vector.Vector2f implements Vector<Vector2f> {

   public static final Vector2f NULL = new Vector2f();

   public Vector2f() {
      super(0, 0);
   }

   public Vector2f(float x, float y) {
      super(x, y);
   }

   public Vector2f rotateOrtho() {
      float t = x;
      x = -y;
      y = t;
      return this;
   }

   public Vector2f(Vector2f v) {
      super(v.x, v.y);
   }

   @Override
   public Vector2f add(Vector2f v) {
      x += v.x;
      y += v.y;
      return this;
   }

   @Override
   public Vector2f sub(Vector2f v) {
      x -= v.x;
      y -= v.y;
      return this;
   }

   @Override
   public Vector2f scale(float factor) {
      x *= factor;
      y *= factor;
      return this;
   }

   @Override
   public Vector2f copy(Vector2f v) {
      x = v.x;
      y = v.y;
      return this;
   }

   public Vector2f copy(float x, float y) {
      this.x = x;
      this.y = y;
      return this;
   }

   @Override
   public String toString() {
      return "[" + x + "," + y + "]";
   }
}