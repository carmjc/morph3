package net.carmgate.morph.ui.renderers.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.slf4j.Logger;

import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.ui.AngelCodeFont;
import net.carmgate.morph.ui.renderers.StringRenderable;

@Singleton
public class RenderUtils {

	public enum TextAlign {
		LEFT,
		CENTER,
		RIGHT;
	}

	private static final int nbSegments = 200;

	private static final double deltaAngle = (float) (2 * Math.PI / nbSegments);
	private static final float cos = (float) Math.cos(deltaAngle);
	private static final float sin = (float) Math.sin(deltaAngle);

	@Inject private Logger LOGGER;

	private final Vec2 ortho = new Vec2();

	public float[] getPartialDiscVertices(float radius, float ratio, float deltaX, float deltaY, int dimension) {
		ratio = Math.max(0, Math.min(1, ratio));
		float[] result = new float[(int) (3 * dimension * nbSegments * ratio)];

		float tExt = 0; // temporary data holder

		final float xInt = 0;
		final float yInt = 0;

		float xExt = radius; // radius
		float xExtBackup = xExt; // radius
		float yExt = 0;
		float yExtBackup = 0;

		for (int i = 0; i < nbSegments * ratio; i++) {
			tExt = xExt;
			xExt = cos * xExt - sin * yExt;
			yExt = sin * tExt + cos * yExt;

			result[3 * dimension * i] = xInt + deltaX;
			result[3 * dimension * i + 1] = yInt + deltaY;

			result[3 * dimension * i + dimension] = xExtBackup + deltaX;
			result[3 * dimension * i + dimension + 1] = yExtBackup + deltaY;

			result[3 * dimension * i + 2 * dimension] = xExt + deltaX;
			result[3 * dimension * i + 2 * dimension + 1] = yExt + deltaY;

			xExtBackup = xExt;
			yExtBackup = yExt;
		}

		return result;
	}

	// TODO The "line" parameter should not be necessary
	// The method should adapt to the number of lines printed so far
	@Deprecated
	public StringRenderable getStringRenderable(AngelCodeFont font, float x, float y, String str, float line, Color color, float targetFontSize) {
		return getStringRenderable(font, x, y, str, line, color, TextAlign.LEFT, targetFontSize);
	}

	@Deprecated
	public StringRenderable getStringRenderable(AngelCodeFont font, float x, float y, String str, float line, Color white, TextAlign align, float targetFontSize) {
		float fontRatio = targetFontSize / font.getLineHeight();
		if (align == TextAlign.RIGHT) {
			x -= font.getWidth(str);
		} else if (align == TextAlign.CENTER) {
			x -= font.getWidth(str) / 2;
		}
		StringRenderable strR = new StringRenderable();
		strR.setStr(str);
		strR.getPos().x = x / fontRatio;
		strR.getPos().y = (y + targetFontSize * (line - 1)) / fontRatio;
		strR.setSize(20);

		return strR;
	}

	@Deprecated
	public StringRenderable getStringRenderable(AngelCodeFont font, String str, float line, Color white, TextAlign align, float targetFontSize) {
		return getStringRenderable(font, 0, 0, str, line, white, align, targetFontSize);
	}

	public TextureImpl getTexture(String resourceName, InputStream in) throws IOException {
		int textureID = InternalTextureLoader.createTextureID();
		int target = GL11.GL_TEXTURE_2D;
		TextureImpl texture = new TextureImpl(resourceName, target, textureID);

		GL11.glBindTexture(target, textureID);

		ByteBuffer textureBuffer;
		int width;
		int height;
		int texWidth;
		int texHeight;

		LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
		textureBuffer = imageData.loadImage(new BufferedInputStream(in), false, null);

		width = imageData.getWidth();
		height = imageData.getHeight();

		texture.setTextureWidth(imageData.getTexWidth());
		texture.setTextureHeight(imageData.getTexHeight());

		texWidth = texture.getTextureWidth();
		texHeight = texture.getTextureHeight();

		IntBuffer temp = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, temp);
		int max = temp.get(0);
		if (texWidth > max || texHeight > max) {
			throw new IOException("Attempt to allocate a texture to big for the current hardware");
		}

		int srcPixelFormat = GL11.GL_RGBA;

		texture.setWidth(width);
		texture.setHeight(height);

		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		// produce a texture from the byte buffer
		GLU.gluBuild2DMipmaps(target, 4,
				InternalTextureLoader.get2Fold(width),
				InternalTextureLoader.get2Fold(height),
				srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		return texture;

	}

	public void renderAntialiasedDisc(float outerRadius, float blurWidthExt, float[] colorMiddle) {
		renderPartialCircle(0, 1f, 0, outerRadius, 0, blurWidthExt, new float[] { 0, 0, 0, 0 },
				colorMiddle,
				new float[] { 0, 0, 0, 0 });
	}

	public void renderAntialiasedPartialDisc(float ratioStart, float ratioEnd, float outerRadius, float[] colorMiddle, float zoom) {
		renderPartialCircle(ratioStart, ratioEnd, 0, outerRadius, 0, 20 / zoom, new float[] { colorMiddle[0], colorMiddle[1], colorMiddle[2], 0 },
				colorMiddle,
				new float[] { colorMiddle[0], colorMiddle[1], colorMiddle[2], 0 });
	}

	public void renderCircle(float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt, float[] colorInt, float[] colorMiddle,
			float[] colorExt) {
		renderPartialCircle(0, 1f, innerRadius, outerRadius, blurWidthInt, blurWidthExt, colorInt, colorMiddle, colorExt);
	}

	public void renderDisc(float radius) {
		renderPartialDisc(radius, 1);
	}

	public void renderLine(Vec2 from, Vec2 to, float widthFrom, float widthTo, float blurWidth, float[] colorInt, float[] colorExt) {
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

	public void renderLine(Vec2 from, Vec2 to, float width, float blurWidth, float[] colorInt, float[] colorExt) {
		renderLine(from, to, width, width, blurWidth, colorInt, colorExt);
	}

	public void renderPartialCircle(float ratioStart, float ratioEnd, float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt,
			float[] colorInt,
			float[] colorMiddle, float[] colorExt) {

		renderPartialSegmentedCircle(ratioStart, ratioEnd, innerRadius, outerRadius, blurWidthInt, blurWidthExt, colorInt, colorMiddle, colorExt, 0);
	}

	public void renderPartialDisc(float radius, float ratio) {
		ratio = Math.max(0, Math.min(1, ratio));

		float tExt = 0; // temporary data holder

		final float xInt = 0;
		final float yInt = 0;

		float xExt = radius; // radius
		float xExtBackup = xExt; // radius
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

	public void renderPartialSegmentedCircle(float ratioStart, float ratioEnd, float innerRadius, float outerRadius, float blurWidthInt,
			float blurWidthExt, float[] colorInt, float[] colorMiddle, float[] colorExt, int nbSubSegments) {
		int currentSubSegment = 0;
		boolean oneSegmentDrawn = false;

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

		for (int i = 0; i < (int) (nbSegments * ratioStart); i++) {
			temp.clear();
			temp.put(x);
			temp.position(0);
			temp.get(t);
			for (int j = 0; j < 4; j++) {
				x[j] = cos * x[j] - sin * y[j];
				y[j] = sin * t[j] + cos * y[j];
			}

			temp.clear();
			temp.put(x);
			temp.position(0);
			temp.get(xBackup);

			temp.clear();
			temp.put(y);
			temp.position(0);
			temp.get(yBackup);
		}

		float remainingNbSegments = nbSegments * (ratioEnd - ratioStart);
		for (int i = 0; i < remainingNbSegments; i++) {
			temp.clear();
			temp.put(x);
			temp.position(0);
			temp.get(t);
			for (int j = 0; j < 4; j++) {
				x[j] = cos * x[j] - sin * y[j];
				y[j] = sin * t[j] + cos * y[j];
			}

			if (nbSubSegments == 0 ||
					i >= remainingNbSegments * (2 * currentSubSegment) / (2 * nbSubSegments)
					&& i < remainingNbSegments * (1 + 2 * currentSubSegment) / (2 * nbSubSegments)) {
				GL11.glBegin(GL11.GL_QUADS);
				// if (innerRadius > 0) {
				GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
				GL11.glVertex2f(x[0], y[0]);
				GL11.glColor4f(colorInt[0], colorInt[1], colorInt[2], colorInt[3]);
				GL11.glVertex2f(xBackup[0], yBackup[0]);
				// }
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

				oneSegmentDrawn = true;
			} else {
				if (oneSegmentDrawn) {
					currentSubSegment++;
					oneSegmentDrawn = false;
				}
			}

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

	/**
	 * Renders a quad.
	 *
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 * @param color
	 */
	public void renderQuad(float left, float top, float right, float bottom, float[] color) {
		TextureImpl.bindNone();
		GL11.glColor4f(color[0], color[1], color[2], color[3]);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(left, top);
		GL11.glVertex2f(right, top);
		GL11.glVertex2f(right, bottom);
		GL11.glVertex2f(left, bottom);
		GL11.glEnd();
	}

	public void renderSegmentedCircle(float innerRadius, float outerRadius, float blurWidthInt, float blurWidthExt, float[] colorInt, float[] colorMiddle,
			float[] colorExt, int nbSegments) {
		renderPartialSegmentedCircle(0, 1f, innerRadius, outerRadius, blurWidthInt, blurWidthExt, colorInt, colorMiddle, colorExt, nbSegments);
	}

	/**
	 * Renders a sprite centered on the current position.
	 *
	 * @param width
	 * @param texture
	 */
	public void renderSprite(final float width, Texture texture) {
		renderSpriteFromBigTexture(width, texture, 0, 0, 1, 1, 1);
	}

	/**
	 * Renders a sprite centered on the current position.
	 *
	 * @param width
	 * @param texture
	 */
	public void renderSprite(final float width, Texture texture, float skewRatio) {
		renderSpriteFromBigTexture(width, texture, 0, 0, 1, 1, skewRatio);
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
	public void renderSpriteFromBigTexture(float width, Texture texture, float texCoordLeft, float texCoordTop, float texCoordRight,
			float texCoordBottom, float skewRatio) {
		texture.bind();

		float absSkewRatio = Math.abs(skewRatio);
		if (skewRatio > 0) {
			skewRatio = 1 / skewRatio;
		}

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f((texCoordLeft + texCoordRight) / 2, (texCoordTop + texCoordBottom) / 2);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(texCoordLeft, texCoordTop);
		GL11.glVertex2f(-width / 2 * absSkewRatio, -width / 2 * skewRatio);
		GL11.glTexCoord2f(texCoordRight, texCoordTop);
		GL11.glVertex2f(width / 2 * absSkewRatio, -width / 2 / skewRatio);
		GL11.glTexCoord2f(texCoordRight, texCoordBottom);
		GL11.glVertex2f(width / 2 * absSkewRatio, width / 2 / skewRatio);
		GL11.glTexCoord2f(texCoordLeft, texCoordBottom);
		GL11.glVertex2f(-width / 2 * absSkewRatio, width / 2 * skewRatio);
		GL11.glTexCoord2f(texCoordLeft, texCoordTop);
		GL11.glVertex2f(-width / 2 * absSkewRatio, -width / 2 * skewRatio);
		GL11.glEnd();
	}

}
