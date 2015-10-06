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
import net.carmgate.morph.model.entities.Planet;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class PlanetRenderer implements Renderer<Planet> {

	private float[] vertices = new float[] { // quadInTrianglesVertices
			-0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, 0.5f, 0,
			0.5f, -0.5f, 0
	};

	private float[] texCoords = new float[] { // quadInTrianglesTexCoords
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};

	@Inject private ShaderManager shaderManager;
	@Inject private Conf conf;
	@Inject private RenderUtils renderUtils;
	@Inject private Logger LOGGER;

	private int progId;
	private FloatBuffer modelToWorldFb;
	private int mLocation;
	private int contColorLocation;
	private Matrix4f m = new Matrix4f();
	private int vc;
	private int vaoId;
	private Texture planetTexture;

	@Override
	public void clean() {
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	@Override
	public void init() {
		initGeometry();
		initTextures();

		progId = shaderManager.getProgram("basic");
		modelToWorldFb = BufferUtils.createFloatBuffer(16);
		mLocation = GL20.glGetUniformLocation(progId, "M");
		contColorLocation = GL20.glGetUniformLocation(progId, "constColor");
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
		if (planetTexture == null) {
			try (BufferedInputStream fileInputStream = new BufferedInputStream(
					ClassLoader.getSystemResourceAsStream(conf.getProperty("planet.renderer.texture")))) { //$NON-NLS-1$
				planetTexture = renderUtils.getTexture("PNG", fileInputStream);
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
	}

	@Override
	public void render(Planet planet, float alpha, FloatBuffer vpFb) {
		float width = 4096;

		m.load(planet.getModelToWorld());
		m.m00 *= width;
		m.m01 *= width;
		m.m10 *= width;
		m.m11 *= width;
		m.store(modelToWorldFb);
		modelToWorldFb.flip();

		planetTexture.bind();
		GL20.glUniform4f(contColorLocation, 1, 1, 1, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(shaderManager.getProgram("basic"), "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);
	}

}
