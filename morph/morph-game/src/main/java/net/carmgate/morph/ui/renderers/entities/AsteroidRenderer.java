package net.carmgate.morph.ui.renderers.entities;

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
import net.carmgate.morph.model.entities.Asteroid;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class AsteroidRenderer implements Renderer<Asteroid> {

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

	private Texture asteroidsTexture;

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private RenderUtils renderUtils;
	@Inject private ShaderManager shaderManager;

	private float massToSizeFactor;

	private int progId;
	private FloatBuffer modelToWorldFb;
	private int mLocation;
	private Matrix4f m = new Matrix4f();
	private int contColorLocation;

	private int vc;

	private int vaoId;

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

		massToSizeFactor = conf.getFloatProperty("asteroid.renderer.massToSizeFactor"); //$NON-NLS-1$

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
		if (asteroidsTexture == null) {
			try (BufferedInputStream fileInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(conf.getProperty("asteroid.renderer.texture")))) { //$NON-NLS-1$
				asteroidsTexture = renderUtils.getTexture("PNG", fileInputStream);
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
	public void render(Asteroid asteroid, float alpha, FloatBuffer vpFb) {
		float massScale = asteroid.getBody().getMass() * massToSizeFactor;
		float width = 128;

		m.load(asteroid.getModelToWorld());
		m.m00 *= width * massScale;
		m.m01 *= width * massScale;
		m.m10 *= width * massScale;
		m.m11 *= width * massScale;
		m.store(modelToWorldFb);
		modelToWorldFb.flip();

		asteroidsTexture.bind();
		GL20.glUniform4f(contColorLocation, 1, 1, 1, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(shaderManager.getProgram("basic"), "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

	}

}
