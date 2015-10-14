package net.carmgate.morph.ui.renderers.animations.ship;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

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
import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.model.geometry.GeoUtils;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class LaserRenderer implements Renderer<LaserAnim> {

	private float[] quadInTrianglesVertices = new float[] {
			-1, -1, 0,
			-1, 1, 0,
			1, -1, 0,
			-1, 1, 0,
			1, 1, 0,
			1, -1, 0
	};

	private float[] quadInTrianglesTexCoords = new float[] {
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};

	@Inject private Conf conf;
	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;
	@Inject private ShaderManager shaderManager;

	private Matrix4f m = new Matrix4f();
	private FloatBuffer mFb;
	private int progId;
	private int mLocation;
	private int constColorLocation;

	private int vc;
	private int vaoId;

	private Texture lineTexture;

	@Override
	public void clean() {
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	@Override
	public void init() {
		initTextures();
		initGeometry();

		progId = shaderManager.getProgram("line");
		mLocation = GL20.glGetUniformLocation(progId, "M");
		constColorLocation = GL20.glGetUniformLocation(progId, "constColor");
	}

	private void initGeometry() {
		FloatBuffer vb = BufferUtils.createFloatBuffer(quadInTrianglesVertices.length);
		vb.put(quadInTrianglesVertices);
		vb.flip();
		vc = quadInTrianglesVertices.length / 3;

		FloatBuffer tb = BufferUtils.createFloatBuffer(quadInTrianglesTexCoords.length);
		tb.put(quadInTrianglesTexCoords);
		tb.flip();

		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vb, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		int tboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tb, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);
	}

	private void initTextures() {
		try (BufferedInputStream lineIs = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(conf.getProperty("line.texture")))) { //$NON-NLS-1$
			// shipBgTexture = renderUtils.getTexture("PNG", shipBgInputStream);
			lineTexture = renderUtils.getTexture("PNG", lineIs);
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
		}
	}

	@PostConstruct
	private void postConstruct() {
		mFb = BufferUtils.createFloatBuffer(16);

	}

	@Override
	public void prepare() {
		GL20.glUseProgram(progId);
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	@Override
	public void render(LaserAnim laserAnim, float alpha, FloatBuffer vpFb) {
		if (!laserAnim.getSource().isActive() || laserAnim.getTarget() == null) {
			return;
		}

		double cosAngleShip = Math.cos(laserAnim.getSource().getShip().getBody().getAngle());
		double sinAngleShip = Math.sin(laserAnim.getSource().getShip().getBody().getAngle());

		Vec2 targetPos = laserAnim.getTarget().getPosition();
		Vec2 posInShip = laserAnim.getSource().getPosInShip();
		Vec2 sourcePos = laserAnim.getSource().getShip().getPosition()
				.add(new Vec2((float) (posInShip.x * cosAngleShip - posInShip.y * sinAngleShip),
						(float) (posInShip.x * sinAngleShip + posInShip.y * cosAngleShip)));
		float dist = GeoUtils.distanceTo(targetPos, sourcePos);
		float deltaX = targetPos.x - sourcePos.x;
		float deltaY = targetPos.y - sourcePos.y;

		m.setIdentity();
		m.m00 = deltaX / 2;
		m.m01 = deltaY / 2;
		m.m10 = -5 * deltaY / dist;
		m.m11 = 5 * deltaX / dist;
		m.m22 = 1;
		m.m30 = sourcePos.x + deltaX / 2;
		m.m31 = sourcePos.y + deltaY / 2;

		m.store(mFb);
		mFb.flip();

		lineTexture.bind();
		GL20.glUniform4f(constColorLocation, 1, 0.3f, 0.3f, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, mFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(progId, "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);
	}

}
