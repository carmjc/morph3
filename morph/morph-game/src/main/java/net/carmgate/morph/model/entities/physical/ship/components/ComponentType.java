package net.carmgate.morph.model.entities.physical.ship.components;

public enum ComponentType {
   GENERATORS(new float[] { 1, 1, 1 }),
   PROPULSORS(new float[] { 0.5f, 0.5f, 0.5f }),
   MINING_LASERS(new float[] { 1, 1, 0 }),
   LASERS(new float[] { 1, 0, 0 }),
   REPAIRER(new float[] { 0, 0, 0.3f });

   final private float[] color;

   private ComponentType(float[] color) {
      this.color = color;
   }

   public float[] getColor() {
      return color;
   }
}
