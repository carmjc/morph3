package net.carmgate.morph.ui.renderers.utils;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.carmgate.morph.model.geometry.Vector2f;

public class RenderUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RenderUtils.class);

	private static final int nbSegments = 100;
	private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
	private static final float cos = (float) Math.cos(deltaAngle);
	private static final float sin = (float) Math.sin(deltaAngle);

	private static final Vector2f ortho = new Vector2f();

	public static void renderCircle(float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt, float[] colorInt, float[] colorMiddle, float[] colorExt) {

		// render limit of effect zone
		TextureImpl.bindNone();
		float[] x = new float[] { 0, 0, 0, 0 };
		float[] y = new float[] { 0, 0, 0, 0 };
		float[] t = new float[] { 0, 0, 0, 0 };
		float[] xBackup = new float[] { 0, 0, 0, 0 };
		float[] yBackup = new float[] { 0, 0, 0, 0 };
		FloatBuffer temp = FloatBuffer.allocate(4);

		x[0] = innerRadius - blurWidthInt; // radius
		x[1] = innerRadius;
		x[2] = outerRadius;
		x[3] = outerRadius + blurWidthExt;

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
		renderPartialDisc(radius, 1);
	}

	public static void renderLine(Vector2f from, Vector2f to, float widthFrom, float widthTo, float blurWidth, float[] colorInt, float[] colorExt) {
		TextureImpl.bindNone();
		ortho.copy(to).sub(from);
		float orthoLength = ortho.length();
		ortho.scale(1 / orthoLength).rotateOrtho();

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
		GL11.glVertex2f(to.x - ortho.x * widthTo / 2 - ortho.x * blurWidth, to.y - ortho.y * widthTo / 2 - ortho.y * blurWidth);
		GL11.glVertex2f(from.x - ortho.x * widthFrom / 2 - ortho.x * blurWidth, from.y - ortho.y * widthFrom / 2 - ortho.y * blurWidth);
		GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
		GL11.glVertex2f(from.x - ortho.x * widthFrom / 2, from.y - ortho.y * widthFrom / 2);
		GL11.glVertex2f(to.x - ortho.x * widthTo / 2, to.y - ortho.y * widthTo / 2);

		GL11.glVertex2f(from.x - ortho.x * widthFrom / 2, from.y - ortho.y * widthFrom / 2);
		GL11.glVertex2f(to.x - ortho.x * widthTo / 2, to.y - ortho.y * widthTo / 2);
		GL11.glVertex2f(to.x + ortho.x * widthTo / 2, to.y + ortho.y * widthTo / 2);
		GL11.glVertex2f(from.x + ortho.x * widthFrom / 2, from.y + ortho.y * widthFrom / 2);

		GL11.glVertex2f(from.x + ortho.x * widthFrom / 2, from.y + ortho.y * widthFrom / 2);
		GL11.glVertex2f(to.x + ortho.x * widthTo / 2, to.y + ortho.y * widthTo / 2);
		GL11.glColor4f(colorExt[0], colorExt[1], colorExt[2], colorExt[3]);
		GL11.glVertex2f(to.x + ortho.x * widthTo / 2 + ortho.x * blurWidth, to.y + ortho.y * widthTo / 2 + ortho.y * blurWidth);
		GL11.glVertex2f(from.x + ortho.x * widthFrom / 2 + ortho.x * blurWidth, from.y + ortho.y * widthFrom / 2 + ortho.y * blurWidth);
		GL11.glEnd();
	}

	public static void renderLine(Vector2f from, Vector2f to, float width, float blurWidth, float[] colorInt, float[] colorExt) {
		renderLine(from, to, width, width, blurWidth, colorInt, colorExt);
	}

	public static void renderPartialDisc(float radius, float ratio) {
		ratio = Math.max(0, Math.min(1, ratio));

		// render limit of effect zone
		TextureImpl.bindNone();
		float tExt = 0; // temporary data holder
		float xInt;
		float xExt;

		xInt = 0; // radius
		xExt = radius; // radius

		float xExtBackup = xExt; // radius
		final float yInt = 0;
		float yExt = 0;
		float yExtBackup = 0;
		for (int i = 0; i < nbSegments * ratio; i++) {

			tExt = xExt;
			xExt = cos * xExt - sin * yExt;
			yExt = sin * tExt + cos * yExt;

			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex2f(xInt, yInt);
			GL11.glVertex2f(xExtBackup, yExtBackup);
			GL11.glVertex2f(xExt, yExt);
			GL11.glEnd();

			xExtBackup = xExt;
			yExtBackup = yExt;
		}
	}

	/**
	 * Renders a quad.
	 *
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public static void renderQuad(float left, float top, float right, float bottom) {
		TextureImpl.bindNone();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(left, top);
		GL11.glVertex2f(right, top);
		GL11.glVertex2f(right, bottom);
		GL11.glVertex2f(left, bottom);
		GL11.glEnd();
	}

	/**
	 * Renders a sprite centered on the current position.
	 *
	 * @param width
	 * @param texture
	 */
	public static void renderSprite(final float width, Texture texture) {
		renderSpriteFromBigTexture(width, texture, 0, 0, 1, 1);
	}

	/**
	 * Render a sprite using a part of a bigger texture.
	 *
	 * @param width
	 * @param texture
	 * @param texCoordLeft
	 * @param texCoordTop
	 * @param texCoordRight
	 * @param texCoordBottom
	 */
	public static void renderSpriteFromBigTexture(float width, Texture texture, float texCoordLeft, float texCoordTop, float texCoordRight, float texCoordBottom) {
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(texCoordLeft, texCoordTop);
		GL11.glVertex2f(-width / 2, -width / 2);
		GL11.glTexCoord2f(texCoordRight, texCoordTop);
		GL11.glVertex2f(width / 2, -width / 2);
		GL11.glTexCoord2f(texCoordRight, texCoordBottom);
		GL11.glVertex2f(width / 2, width / 2);
		GL11.glTexCoord2f(texCoordLeft, texCoordBottom);
		GL11.glVertex2f(-width / 2, width / 2);
		GL11.glEnd();
	}

	// TODO The "line" parameter should not be necessary
	// The method should adapt to the number of lines printed so far
	public static void renderText(TrueTypeFont font, float x, float y, String str, int line, Color color) {
		renderText(font, x, y, str, line, color, true);
	}

	public static void renderText(TrueTypeFont font, float x, float y, String str, int line, Color color, boolean alignLeft) {
		if (!alignLeft) {
			x -= font.getWidth(str);
		}
		font.drawString(x, y + font.getHeight() * (line - 1), str, color);
	}
}
