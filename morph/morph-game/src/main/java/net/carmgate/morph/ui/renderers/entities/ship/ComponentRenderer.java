package net.carmgate.morph.ui.renderers.entities.ship;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

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
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.components.repair.SimpleRepairer;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class ComponentRenderer implements Renderer<Component> {

	@Inject private Logger LOGGER;
	@Inject private RenderUtils renderUtils;
	@Inject private Conf conf;
	@Inject private MWorld world;
	@Inject private ComponentManager componentManager;

	private Map<ComponentType, Texture> cmpTextures = new HashMap<>();
	private int progId;
	private int vaoId;
	private float[] texCoords = new float[] { // quadInTrianglesTexCoords
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};
	private int vc;
	private float[] vertices = new float[] { // quadInTrianglesVertices
			-0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, -0.5f, 0,
			-0.5f, 0.5f, 0,
			0.5f, 0.5f, 0,
			0.5f, -0.5f, 0
	};
	private int contColorLocation;
	private int mLocation;
	private FloatBuffer modelToWorldFb;
	@Inject private ShaderManager shaderManager;
	private Matrix4f m = new Matrix4f();

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
		try (BufferedInputStream laserInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
				conf.getProperty(Laser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream mlInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(MiningLaser.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream repairerInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimpleRepairer.class.getCanonicalName() + ".renderer.texture"))); //$NON-NLS-1$
				BufferedInputStream propInputStream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty(SimplePropulsor.class.getCanonicalName() + ".renderer.texture")))) {
			cmpTextures.put(ComponentType.LASERS, renderUtils.getTexture("PNG", laserInputStream));
			cmpTextures.put(ComponentType.MINING_LASERS, renderUtils.getTexture("PNG", mlInputStream));
			cmpTextures.put(ComponentType.REPAIRER, renderUtils.getTexture("PNG", repairerInputStream));
			cmpTextures.put(ComponentType.PROPULSORS, renderUtils.getTexture("PNG", propInputStream));
		} catch (IOException e) {
			LOGGER.error("Exception raised while loading texture", e); //$NON-NLS-1$
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
	public void render(Component cmp, float alpha, FloatBuffer vpFb) {
		final Ship ship = cmp.getShip();
	}

	public void render(Component cmp, float alpha, Matrix4f mTmp, FloatBuffer vpFb) {

		final float width = 50;

		// draw the component
		Texture texture = cmpTextures.get(cmp.getClass().getAnnotation(ComponentKind.class).value());

		// if (componentManager.getAvailability(cmp) < 1 && ship == world.getPlayerShip()) {
		// GL11.glRotatef(-ship.getBody().getAngle() - 90, 0, 0, 1);
		// renderUtils.renderAntialiasedPartialDisc(0 + componentManager.getAvailability(cmp), 1, width / 2 - 10,
		// new float[] { 0.3f, 0.3f, 0.3f, 0.8f * alpha }, 1);
		// GL11.glRotatef(ship.getBody().getAngle() + 90, 0, 0, 1);
		// }

		// m.load(ship.getModelToWorld());
		m.load(mTmp);
		m.m00 *= width;
		m.m01 *= width;
		m.m10 *= width;
		m.m11 *= width;

		// Color color = new Color(Color.white);
		// GL11.glColor4f(color.r, color.g, color.b, color.a * alpha);
		// if (texture != null) {
		// renderUtils.renderSprite(width, texture);
		// }

		m.store(modelToWorldFb);
		modelToWorldFb.flip();

		if (texture != null) {
			texture.bind();
		}
		GL20.glUniform4f(contColorLocation, 1, 1, 1, 1);
		GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(progId, "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vc);

	}

}
