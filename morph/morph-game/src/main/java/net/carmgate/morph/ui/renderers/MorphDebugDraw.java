package net.carmgate.morph.ui.renderers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.geometry.GeoUtils;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class MorphDebugDraw extends DebugDraw {

	private Texture lineTexture;
	private Texture circleTexture;
	@Inject private Conf conf;
	private int constColorLocation;
	@Inject private Logger LOGGER;
	private Matrix4f m = new Matrix4f();
	private int mLocation;
	private FloatBuffer mFb;
	private int progId;
	@Inject private RenderUtils renderUtils;
	@Inject private ShaderManager shaderManager;
	@Inject private UIContext uiContext;

	private float[] texCoords = new float[] { // quadInTrianglesTexCoords
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};
	private int vaoId;
	private int vc;
	private float[] vertices = new float[] { // quadInTrianglesVertices
			-0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, 0.5f, 0,
			0.5f, -0.5f, 0
	};
	private FloatBuffer vpFb;
	private float alpha;
	private boolean gui;

	public MorphDebugDraw() {
		super(new OBBViewportTransform());
	}

	public MorphDebugDraw(IViewportTransform viewport) {
		super(viewport);
	}

	public void clean() {
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		prepare();

		float width = radius * 2000;

		m.setIdentity();
		m.m00 *= width;
		m.m01 *= width;
		m.m10 *= width;
		m.m11 *= width;
		if (gui) {
			m.m30 = center.x * 1000 - uiContext.getWindow().getWidth() / 2;
			m.m31 = center.y * 1000 - uiContext.getWindow().getHeight() / 2;
		} else {
			m.m30 = center.x * 1000;
			m.m31 = center.y * 1000;
		}
		m.store(mFb);
		mFb.flip();

		circleTexture.bind();
		GL20.glUniform4f(constColorLocation, color.x * 1, color.y * 1, color.z * 1, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, mFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(shaderManager.getProgram("basic"), "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

		clean();
	}

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
		drawCircle(argPoint, argRadiusOnScreen / 1000, argColor);
	}

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		prepare();

		float dist = GeoUtils.distanceTo(p2, p1);
		float deltaX = p2.x - p1.x;
		float deltaY = p2.y - p1.y;

		m.setIdentity();
		m.m00 = deltaX * 1000;
		m.m01 = deltaY * 1000;
		m.m10 = -5 * deltaY / dist;
		m.m11 = 5 * deltaX / dist;
		m.m22 = 1;
		if (gui) {
			m.m30 = p1.x * 1000 + deltaX * 1000 / 2 - uiContext.getWindow().getWidth() / 2;
			m.m31 = p1.y * 1000 + deltaY * 1000 / 2 - uiContext.getWindow().getHeight() / 2;
		} else {
			m.m30 = p1.x * 1000 + deltaX * 1000 / 2;
			m.m31 = p1.y * 1000 + deltaY * 1000 / 2;
		}

		m.store(mFb);
		mFb.flip();

		lineTexture.bind();
		GL20.glUniform4f(constColorLocation, color.x, color.y, color.z, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, mFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(progId, "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

		clean();
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
		drawCircle(center, radius, color);
		drawSegment(center, center.add(axis.mul(radius)), color);
	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		for (int i = 0; i < vertexCount - 1; i++) {
			drawSegment(vertices[i], vertices[i + 1], color);
		}
	}

	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		// LOGGER.debug("Debug drawString");
	}

	@Override
	public void drawTransform(Transform xf) {
		// ...
	}

	public void init() {
		initGeometry();
		initTextures();

		progId = shaderManager.getProgram("basic");
		mFb = BufferUtils.createFloatBuffer(16);
		mLocation = GL20.glGetUniformLocation(progId, "M");
		constColorLocation = GL20.glGetUniformLocation(progId, "constColor");
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
		try (BufferedInputStream lineIs = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(conf.getProperty("line.texture")));
				BufferedInputStream circleIs = new BufferedInputStream(
						ClassLoader.getSystemResourceAsStream(conf.getProperty("circle.texture")))) { //$NON-NLS-1$
			lineTexture = renderUtils.getTexture("PNG", lineIs);
			circleTexture = renderUtils.getTexture("PNG", circleIs);
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
		}
	}

	public void prepare() {
		GL20.glUseProgram(progId);
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	public void updateWorld(float alpha, FloatBuffer vpFb, boolean gui) {
		this.alpha = alpha;
		this.vpFb = vpFb;
		this.gui = gui;
	}

}
