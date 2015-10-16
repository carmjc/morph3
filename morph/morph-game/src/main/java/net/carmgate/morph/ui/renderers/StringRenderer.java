package net.carmgate.morph.ui.renderers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.ui.AngelCodeFont;
import net.carmgate.morph.ui.AngelCodeFont.Glyph;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.TextAlign;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class StringRenderer implements Renderer<StringRenderable> {

	private float[] vertices = new float[] { // quadInTrianglesVertices
			0f, 0f, 0,
			0f, 1f, 0,
			1f, 0f, 0,
			0f, 1f, 0,
			1f, 1f, 0,
			1f, 0f, 0
	};

	private float[] texCoords = new float[] { // quadInTrianglesTexCoords
			0, 1,
			0, 0,
			1, 1,
			0, 0,
			1, 0,
			1, 1
	};

	private Texture fontTexture;

	@Inject private RenderUtils renderUtils;
	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private ShaderManager shaderManager;

	private AngelCodeFont font;
	private int mLocation;
	private FloatBuffer modelToWorldFb;
	private Matrix4f m = new Matrix4f();
	private int contColorLocation;
	private int progId;
	private int vc;
	private int vaoId;
	private int fontDataLocation;
	private float[] fontData;
	private FloatBuffer fontDataFb;

	private int charDataLocation;

	private int vpLocation;

	private int charSizeLocation;

	@Override
	public void clean() {
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	@Override
	public void init() {
		font = new AngelCodeFont(conf.getProperty("ui.font.angel"), conf.getProperty("ui.font.angel.tga"));

		initGeometry();
		initTextures();

		progId = shaderManager.getProgram("font");
		modelToWorldFb = BufferUtils.createFloatBuffer(16);
		mLocation = GL20.glGetUniformLocation(progId, "M");
		contColorLocation = GL20.glGetUniformLocation(progId, "constColor");
		fontDataLocation = GL20.glGetUniformLocation(progId, "fontData");
		charDataLocation = GL20.glGetUniformLocation(progId, "charData");
		charSizeLocation = GL20.glGetUniformLocation(progId, "charSize");
		vpLocation = GL20.glGetUniformLocation(progId, "VP");

		fontData = new float[255 * 4];
		for (int i = 0; i < 255; i++) {
			Glyph glyph = font.getGlyph((char) i);
			if (glyph != null) {
				fontData[4 * i] = (float) glyph.x / font.getScaleW();
				fontData[4 * i + 1] = (float) glyph.y / font.getScaleH();
				fontData[4 * i + 2] = (float) glyph.width / font.getScaleW();
				fontData[4 * i + 3] = (float) glyph.height / font.getScaleH();
			}
		}
		fontDataFb = BufferUtils.createFloatBuffer(fontData.length);
		fontDataFb.put(fontData);
		fontDataFb.flip();
	}

	private void initGeometry() {
		FloatBuffer vb = BufferUtils.createFloatBuffer(vertices.length);
		vb.put(vertices);
		vb.flip();
		vc = vertices.length / 3;
		FloatBuffer tb = BufferUtils.createFloatBuffer(texCoords.length);
		tb.put(texCoords);
		tb.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vb, GL15.GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// tex buffer
		int tboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tb, GL15.GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

	}

	private void initTextures() {
		// load texture from PNG file if needed
		if (fontTexture == null) {
			try (BufferedInputStream fileInputStream = new BufferedInputStream(
					ClassLoader.getSystemResourceAsStream(font.getImgFile()))) {
				fontTexture = renderUtils.getTexture("PNG", fileInputStream);
			} catch (IOException e) {
				LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void prepare() {
		GL20.glUseProgram(progId);
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glUniform4(fontDataLocation, fontDataFb);
	}

	@Override
	public void render(StringRenderable str, float alpha, FloatBuffer vpFb) {

		m.setIdentity();

		render(str, alpha, vpFb, m);
	}


	public void render(StringRenderable str, float alpha, FloatBuffer vpFb, Matrix4f m) {
		float maxWidth = 0;
		for (int i = 0; i < str.getStr().length(); i++) {
			int currentLineWidth = 0;
			char c = str.getStr().charAt(i);
			if (c == 10) {
				currentLineWidth = 0;
			} else {
				Glyph g = font.getGlyph(c);
				currentLineWidth += g.xadvance * str.getSize() / font.getSize();
				maxWidth = Math.max(maxWidth, currentLineWidth);
			}
		}

		TextAlign align = TextAlign.RIGHT;

		GL20.glUniform1f(charSizeLocation, (float) font.getSize() / font.getScaleW());
		GL20.glUniform4f(contColorLocation, 0, 0, 0, 1 * alpha);

		float size = str.getSize();
		m.m00 *= size;
		m.m01 *= size;
		m.m10 *= size;
		m.m11 *= size;
		m.m30 += str.getPos().x;
		m.m31 += str.getPos().y - font.getAscent() * str.getSize() / font.getSize();
		switch (align) {
		case RIGHT:
			m.m30 -= maxWidth / 2;
		case CENTER:
			m.m30 -= maxWidth / 2;
		}

		float cumulatedAdvance = 0;
		for (int i = 0; i < str.getStr().length(); i++) {
			char c = str.getStr().charAt(i);

			if (c == 10) {
				m.m30 -= cumulatedAdvance;
				m.m31 -= font.getLineHeight() * str.getSize() / font.getSize();

				cumulatedAdvance = 0;
				continue;
			}

			Glyph g = font.getGlyph(c);

			float xOffset = g.xoffset * str.getSize() / font.getSize();
			float yOffset = g.yoffset * str.getSize() / font.getSize();

			m.m30 += xOffset;
			m.m31 -= yOffset;
			m.store(modelToWorldFb);
			modelToWorldFb.flip();

			fontTexture.bind();
			GL20.glUniform1i(charDataLocation, c);
			GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
			GL20.glUniformMatrix4(vpLocation, false, vpFb);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

			m.m30 -= xOffset;
			m.m31 += yOffset;
			m.m30 += g.xadvance * str.getSize() / font.getSize();
			cumulatedAdvance += g.xadvance * str.getSize() / font.getSize();

		}
	}

}
