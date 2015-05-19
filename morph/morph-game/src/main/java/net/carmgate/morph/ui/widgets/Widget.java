package net.carmgate.morph.ui.widgets;

public abstract class Widget {

   private float[] position = new float[2];
   private int id;

   public abstract void renderWidget();

   public float[] getPosition() {
      return position;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setPosition(float[] position) {
      this.position = position;
   }

   public int getId() {
      return id;
   }
}
