package net.carmgate.morph.ui.renderers;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class RenderUtils {

   private static final int nbSegments = 100;
   private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
   private static final float cos = (float) Math.cos(deltaAngle);
   private static final float sin = (float) Math.sin(deltaAngle);

   public static void renderCircle(float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt, float[] colorInt, float[] colorMiddle, float[] colorExt) {

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
         GL11.glVertex2f(xInt, yInt);
         GL11.glVertex2f(xIntBackup, yIntBackup);
         GL11.glVertex2f((xExtBackup + xIntBackup) / 2, (yExtBackup + yIntBackup) / 2);
         GL11.glVertex2f((xExt + xInt) / 2, (yExt + yInt) / 2);
         GL11.glVertex2f((xExtBackup + xIntBackup) / 2, (yExtBackup + yIntBackup) / 2);
         GL11.glVertex2f((xExt + xInt) / 2, (yExt + yInt) / 2);
         GL11.glVertex2f(xExt, yExt);
         GL11.glVertex2f(xExtBackup, yExtBackup);
         GL11.glEnd();

         xIntBackup = xInt;
         xExtBackup = xExt;
         yIntBackup = yInt;
         yExtBackup = yExt;
      }
   }

}
