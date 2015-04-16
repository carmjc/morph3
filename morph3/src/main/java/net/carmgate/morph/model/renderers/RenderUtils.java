package net.carmgate.morph.model.renderers;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

public class RenderUtils {

   private static final int nbSegments = 100;
   private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
   private static final float cos = (float) Math.cos(deltaAngle);
   private static final float sin = (float) Math.sin(deltaAngle);

   // Change the logic to make the font private
   public static TrueTypeFont font;

   public static void renderCircle(float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt, Float[] colorInt, Float[] colorMiddle, Float[] colorExt) {

      // render limit of effect zone
      TextureImpl.bindNone();
      float[] x = new float[] { 0, 0, 0, 0 };
      float[] y = new float[] { 0, 0, 0, 0 };
      float[] t = new float[] { 0, 0, 0, 0 };
      float[] xBackup = new float[] { 0, 0, 0, 0 };
      float[] yBackup = new float[] { 0, 0, 0, 0 };
      FloatBuffer temp = FloatBuffer.allocate(4);

      x[0] = innerRadius; // radius
      x[1] = innerRadius + blurWidthInt;
      x[2] = outerRadius - blurWidthExt;
      x[3] = outerRadius;

      temp.clear();
      temp.put(x);
      temp.position(0);
      temp.get(xBackup);
      for (int i = 0; i < nbSegments; i++) {

         temp.clear();
         temp.put(x);
         temp.position(0);
         temp.get(t);
         for (int j = 0; j < 4; j++) {
            x[j] = cos * x[j] - sin * y[j];
            y[j] = sin * t[j] + cos * y[j];
         }
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
         GL11.glVertex2f(x[0], y[0]);
         GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
         GL11.glVertex2f(xBackup[0], yBackup[0]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(xBackup[1], yBackup[1]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(x[1], y[1]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(xBackup[1], yBackup[1]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(x[1], y[1]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(x[2], y[2]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(xBackup[2], yBackup[2]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(x[2], y[2]);
         GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f(xBackup[2], yBackup[2]);
         GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
         GL11.glVertex2f(xBackup[3], yBackup[3]);
         GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
         GL11.glVertex2f(x[3], y[3]);
         GL11.glEnd();

         temp.clear();
         temp.put(x);
         temp.position(0);
         temp.get(xBackup);

         temp.clear();
         temp.put(y);
         temp.position(0);
         temp.get(yBackup);
      }
   }

   public static void renderDisc(float radius) {
      // render limit of effect zone
      TextureImpl.bindNone();
      final float tInt = 0; // temporary data holder
      float tExt = 0; // temporary data holder
      float xInt;
      float xExt;

      xInt = 0; // radius
      xExt = radius; // radius

      float xIntBackup = xInt; // radius
      float xExtBackup = xExt; // radius
      final float yInt = 0;
      float yExt = 0;
      float yIntBackup = 0;
      float yExtBackup = 0;
      for (int i = 0; i < nbSegments; i++) {

         tExt = xExt;
         xExt = cos * xExt - sin * yExt;
         yExt = sin * tExt + cos * yExt;

         GL11.glBegin(GL11.GL_QUADS);
         // GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
         GL11.glVertex2f(xInt, yInt);
         // GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
         GL11.glVertex2f(xIntBackup, yIntBackup);
         // GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f((xExtBackup + xIntBackup) / 2, (yExtBackup + yIntBackup) / 2);
         // GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f((xExt + xInt) / 2, (yExt + yInt) / 2);
         // GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f((xExtBackup + xIntBackup) / 2, (yExtBackup + yIntBackup) / 2);
         // GL11.glColor4f(colorMiddle[0], colorMiddle[1], colorMiddle[2], colorMiddle[3]);
         GL11.glVertex2f((xExt + xInt) / 2, (yExt + yInt) / 2);
         // GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
         GL11.glVertex2f(xExt, yExt);
         // GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
         GL11.glVertex2f(xExtBackup, yExtBackup);
         GL11.glEnd();

         xIntBackup = xInt;
         xExtBackup = xExt;
         yIntBackup = yInt;
         yExtBackup = yExt;
      }
   }

   /**
    * Renders a gauge at the given position
    *
    * @param width
    * @param yGaugePosition
    * @param percentage
    * @param alarmThreshold
    * @param color
    */
   public static void renderGauge(float width, float yGaugePosition, float percentage, float alarmThreshold, float[] color) {

      TextureImpl.bindNone();
      GL11.glColor4f(0.5f, 0.5f, 0.5f, 10);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(width / 2 + 2, yGaugePosition - 5);
      GL11.glVertex2f(width / 2 + 2, yGaugePosition + 5);
      GL11.glVertex2f(-(width / 2 + 2), yGaugePosition + 5);
      GL11.glVertex2f(-(width / 2 + 2), yGaugePosition - 5);
      GL11.glEnd();

      GL11.glColor4f(0, 0, 0, 1);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(width / 2 + 1, yGaugePosition - 4);
      GL11.glVertex2f(width / 2 + 1, yGaugePosition + 4);
      GL11.glVertex2f(-(width / 2 + 1), yGaugePosition + 4);
      GL11.glVertex2f(-(width / 2 + 1), yGaugePosition - 4);
      GL11.glEnd();

      if (percentage < alarmThreshold) {
         GL11.glColor4f(1, 0.5f, 0.5f, 0.5f);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glVertex2f(width / 2, yGaugePosition - 3);
         GL11.glVertex2f(width / 2, yGaugePosition + 3);
         GL11.glVertex2f(-width / 2, yGaugePosition + 3);
         GL11.glVertex2f(-width / 2, yGaugePosition - 3);
         GL11.glEnd();
      }

      GL11.glColor4f(color[0], color[1], color[2], color[3]);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(-width / 2 + percentage * width / 2 * 2, yGaugePosition - 3);
      GL11.glVertex2f(-width / 2 + percentage * width / 2 * 2, yGaugePosition + 3);
      GL11.glVertex2f(-width / 2, yGaugePosition + 3);
      GL11.glVertex2f(-width / 2, yGaugePosition - 3);
      GL11.glEnd();

   }

   // public static void renderLine(Vect3D from, Vect3D to, float width, Float[] colorInt, Float[] colorExt) {
   // TextureImpl.bindNone();
   // Vect3D ortho = new Vect3D(to).substract(from).normalize(width / 2).rotate(90);
   // GL11.glBegin(GL11.GL_QUADS);
   // GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
   // GL11.glVertex2f(from.x - ortho.x, from.y - ortho.y);
   // GL11.glVertex2f(to.x - ortho.x, to.y - ortho.y);
   // GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
   // GL11.glVertex2f(to.x, to.y);
   // GL11.glVertex2f(from.x, from.y);
   // GL11.glVertex2f(to.x, to.y);
   // GL11.glVertex2f(from.x, from.y);
   // GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
   // GL11.glVertex2f(from.x + ortho.x, from.y + ortho.y);
   // GL11.glVertex2f(to.x + ortho.x, to.y + ortho.y);
   // GL11.glEnd();
   // }

   // TODO The "line" parameter should not be necessary
   // The method should adapt to the number of lines printed so far
   // public static void renderLineToConsole(String str, int line) {
   // font.drawString(-Model.getModel().getWindow().getWidth() / 2 + 5,
   // Model.getModel().getWindow().getHeight() / 2 - 3 - font.getHeight() * line,
   // str, Color.white);
   // }

}
