package net.carmgate.morph.ui.widgets.radar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jbox2d.common.Vec2;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.GeoUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;
import net.carmgate.morph.ui.widgets.Widget;

public class RadarWidget extends Widget {

	@Inject private RenderUtils renderUtils;
	@Inject private MWorld world;
	@Inject private ShaderManager shaderManager;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;

	private Texture radarTexture;
	private int contColorLocation;
	private Matrix4f m = new Matrix4f();
	private int mLocation;
	private FloatBuffer modelToWorldFb;
	private int progId;
	private int vaoId;
	private int vc;

	private float[] texCoords = new float[] { // quadInTrianglesTexCoords
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};
	private float[] vertices = new float[] { // quadInTrianglesVertices
			-0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, 0.5f, 0,
			0.5f, -0.5f, 0
	};
	private TextureImpl smallDiscTexture;

	public void clean() {
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	@PostConstruct
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
		if (radarTexture == null) {
			try (BufferedInputStream radarIs = new BufferedInputStream(
					ClassLoader.getSystemResourceAsStream(conf.getProperty("radar.texture")));
					BufferedInputStream smallDiscIs = new BufferedInputStream(
							ClassLoader.getSystemResourceAsStream(conf.getProperty("smallDisc.texture")))) {
				//$NON-NLS-1$
				radarTexture = renderUtils.getTexture("PNG", radarIs);
				smallDiscTexture = renderUtils.getTexture("PNG", smallDiscIs);
			} catch (IOException e) {
				LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
			}
		}
	}

	public void prepare() {
		GL20.glUseProgram(progId);
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	@Override
	public void renderWidget(Matrix4f mTmp, FloatBuffer vpFb) {

		prepare();

		m.load(mTmp);
		m.m00 *= getWidth();
		m.m01 *= getWidth();
		m.m10 *= getWidth();
		m.m11 *= getWidth();
		m.m30 += getWidth() / 2;
		m.m31 -= getHeight() / 2;
		m.store(modelToWorldFb);
		modelToWorldFb.flip();

		radarTexture.bind();
		GL20.glUniform4f(contColorLocation, 1, 1, 1, 1);
		GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(progId, "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

		clean();

		Ship playerShip = world.getPlayerShip();
		float radarRadius = playerShip.getPerceptionRadius();
		float outerRadius = radarRadius / 8;
		float interactionDistanceSq = radarRadius * radarRadius;
		float appearanceDistanceSq = (radarRadius + outerRadius) * (radarRadius + outerRadius);

		Vec2 toShip = new Vec2();
		for (Ship ship : world.getShips()) {
			if (ship != playerShip) {
				prepare();

				m.load(mTmp);
				m.m00 *= 3;
				m.m01 *= 3;
				m.m10 *= 3;
				m.m11 *= 3;
				m.m30 += getWidth() / 2;
				m.m31 -= getHeight() / 2;

				float distanceToShipSq = GeoUtils.distanceToSquared(ship.getPosition(), playerShip.getPosition());
				if (distanceToShipSq < appearanceDistanceSq) {
					float ratio = 1;
					if (distanceToShipSq > interactionDistanceSq) {
						ratio = (appearanceDistanceSq - distanceToShipSq) / (appearanceDistanceSq - interactionDistanceSq);
					}
					toShip.set(ship.getPosition().sub(playerShip.getPosition()).mul(getWidth() / 2 / radarRadius));

					m.m30 += toShip.x;
					m.m31 += toShip.y;
					m.store(modelToWorldFb);
					modelToWorldFb.flip();

					smallDiscTexture.bind();
					GL20.glUniform4f(contColorLocation, 1, 0, 0, 1);
					GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
					GL20.glUniformMatrix4(GL20.glGetUniformLocation(progId, "VP"), false, vpFb);
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

					m.m30 -= toShip.x;
					m.m31 -= toShip.y;
				}

				clean();
			}
		}
	}

}
