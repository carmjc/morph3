package net.carmgate.morph.ui.renderers.entities.ship;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.DragContext;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class ShipRenderer implements Renderer<Ship> {

	private float[] quadInTrianglesVertices = new float[] {
			-100, -100, 0,
			-100, 100, 0,
			100, -100, 0,
			-100, 100, 0,
			100, 100, 0,
			100, -100, 0
	};

	private float[] quadInTrianglesTexCoords = new float[] {
			0, 0,
			0, 1,
			1, 0,
			0, 1,
			1, 1,
			1, 0
	};

	private Texture shipTexture;
	private Texture shipOwnershipTexture;
	private int shipVc;
	private int vaoId;

	@Inject private UIContext uiContext;
	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;
	@Inject private DragContext dragContext;
	@Inject private RenderUtils renderUtils;
	@Inject private ComponentRenderer componentRenderer;
	@Inject private ShaderManager shaderManager;

	private int progId;
	private FloatBuffer modelToWorldFb;
	private int mLocation;
	private int contColorLocation;

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

		progId = shaderManager.getProgram("basic");
		modelToWorldFb = BufferUtils.createFloatBuffer(16);
		mLocation = GL20.glGetUniformLocation(progId, "M");
		contColorLocation = GL20.glGetUniformLocation(progId, "constColor");
	}

	private void initGeometry() {
		FloatBuffer vb = BufferUtils.createFloatBuffer(quadInTrianglesVertices.length);
		vb.put(quadInTrianglesVertices);
		vb.flip();
		shipVc = quadInTrianglesVertices.length / 3;
		FloatBuffer tb = BufferUtils.createFloatBuffer(quadInTrianglesTexCoords.length);
		tb.put(quadInTrianglesTexCoords);
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
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tb, GL15.GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

	}

	private void initTextures() {
		try (BufferedInputStream shipOwnershipIs = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(conf.getProperty("ship.renderer.texture.ownership"))); //$NON-NLS-1$
				BufferedInputStream shipIs = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
						conf.getProperty("ship.renderer.texture")))) { //$NON-NLS-1$
			// shipBgTexture = renderUtils.getTexture("PNG", shipBgInputStream);
			shipTexture = renderUtils.getTexture("PNG", shipIs);
			shipOwnershipTexture = renderUtils.getTexture("PNG", shipOwnershipIs);
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
	public void render(Ship ship, float alpha, FloatBuffer vpFb) {

		// Bind to the VAO that has all the information about the quad vertices

		ship.getModelToWorld().store(modelToWorldFb);
		modelToWorldFb.flip();

		// selection ship rendering
		// if (ship.getPlayer().getPlayerType() == PlayerType.PLAYER) { // Change this with selection test
		// GL20.glUseProgram(selectionProgId);
		// GL30.glBindVertexArray(shipVaoId);
		// GL20.glEnableVertexAttribArray(0);
		// GL20.glEnableVertexAttribArray(1);
		//
		// shipTexture.bind();
		// GL20.glUniform4f(contColorLocationForBasic, 1, 1, 1, 1 * alpha);
		// GL20.glUniformMatrix4(mIDForBasic, false, modelToWorldFb);
		// GL20.glUniformMatrix4(GL20.glGetUniformLocation(shaderManager.getProgram("selection"), "VP"), false, vpBuffer);
		//
		// GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, shipVc);
		// GL20.glDisableVertexAttribArray(1);
		// GL20.glDisableVertexAttribArray(0);
		// GL30.glBindVertexArray(0);
		// GL20.glUseProgram(0);
		// } else {

		shipTexture.bind();
		GL20.glUniform4f(contColorLocation, 1, 1, 1, 1 * alpha);
		GL20.glUniformMatrix4(mLocation, false, modelToWorldFb);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(shaderManager.getProgram("basic"), "VP"), false, vpFb);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, shipVc);

		shipOwnershipTexture.bind();
		float[] ownerShipColor = ship.getPlayer().getColor();
		GL20.glUniform4f(contColorLocation, ownerShipColor[0], ownerShipColor[1], ownerShipColor[2], ownerShipColor[3] * alpha);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, shipVc);

		// Put everything back to default (deselect)

		// final float massScale = ship.getMass();
		// final float width = 200;
		// float zoom = uiContext.getViewport().getZoomFactor();
		//
		// // GL11.glScalef(massScale, massScale, 0);
		// GL11.glColor4f(1f, 1f, 1f, 0.6f * alpha);
		// GL11.glRotatef(ship.getRotation(), 0, 0, 1);
		//
		// // FIXME #23
		// if (ship == uiContext.getSelectedShip()) {
		// float colorScale = (int) (world.getTime() % 1000);
		// colorScale = (colorScale > 500 ? 1000 - colorScale : colorScale) / 1000 * 2 + 0.6f;
		// renderUtils.renderCircle(width / 4f - 0 / massScale,
		// width / 4f - 0 / massScale,
		// 2 / zoom / massScale,
		// 5 / zoom / massScale,
		// new float[] { 0f, 0f, 0f, 0f },
		// new float[] { 1f, 1f, 1f, 0.5f * colorScale * alpha },
		// new float[] { 0f, 0f, 0f, 0f });
		// }
		//
		// float[] color = ship.getPlayer().getColor();
		// GL11.glColor4f(1, 1, 1, alpha);
		// float skewRatio = 1f;
		// if (ship.getRotationSpeed() > 0) {
		// skewRatio = 0.9f;
		// } else if (ship.getRotationSpeed() < 0) {
		// skewRatio = 1 / 0.9f;
		// }
		//
		// renderUtils.renderSprite(width, shipTexture, skewRatio);
		// GL11.glColor4f(color[0], color[1], color[2], color[3] * alpha);
		// renderUtils.renderSprite(width, shipOwnershipTexture, 1);
		// renderComponents(ship, alpha);
		//
		// GL11.glRotatef(-ship.getRotation(), 0, 0, 1);
		//
		// // Render energy and resource gauges
		// float delta = Math.max(2, 2 / zoom);
		// float thickness = Math.max(2f, 2f / zoom);
		// float blurriness = 2 / zoom;
		// float offset = 0.2f; // must be inferior to 0.25
		// if (ship == uiContext.getSelectedShip()) {
		// // Energy
		// if (ship.getEnergyMax() > 0) {
		// renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 0.6f, 0.6f, 0.6f, 1 },
		// new float[] { 0, 0, 0, 0f });
		// renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getEnergy() / ship.getEnergyMax(),
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 0, 0.5f, 1, 1 },
		// new float[] { 0, 0, 0, 0f });
		// }
		//
		// // Resources
		// if (ship.getResourcesMax() > 0) {
		// GL11.glScalef(-1, 1, 1);
		// renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 0.6f, 0.6f, 0.6f, 1 },
		// new float[] { 0, 0, 0, 0f });
		// renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getResources() / ship.getResourcesMax(),
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 139f / 255, 90f / 255, 43f / 255, 1 },
		// new float[] { 0, 0, 0, 0f });
		// GL11.glScalef(-1, 1, 1);
		// }
		//
		// // Integrity
		// GL11.glRotatef(90, 0, 0, 1);
		// GL11.glScalef(-1, 1, 1);
		// renderUtils.renderPartialCircle(0.25f + offset, 0.75f - offset,
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 0.6f, 0.6f, 0.6f, 1 },
		// new float[] { 0, 0, 0, 0f });
		// renderUtils.renderPartialCircle(0.25f + offset, 0.25f + offset + (0.5f - 2 * offset) * ship.getIntegrity(),
		// width / 3f + delta - thickness, width / 3f + delta + thickness, blurriness, blurriness,
		// new float[] { 0, 0, 0, 0f },
		// new float[] { 0.8f, 0.0f, 0.0f, 1 },
		// new float[] { 0, 0, 0, 0f });
		// GL11.glScalef(-1, 1, 1);
		// GL11.glRotatef(-90, 0, 0, 1);
		// }
		//
		// // GL11.glScalef(1f / massScale, 1f / massScale, 0);
		//
		// if (ship == uiContext.getSelectedShip()) {
		// GL11.glTranslatef(0, -width / 4f, 0);
		// renderUtils.renderText(RenderingManager.font, "XP: " + ship.getXp(), 0, Color.white, TextAlign.CENTER);
		// GL11.glTranslatef(0, width / 4f, 0);
		// }
		//
		// // draw component range
		// Component selectedCmp = uiContext.getSelectedCmp();
		// if (selectedCmp != null && selectedCmp.getShip() == ship && selectedCmp.getRange() != 0 && dragContext.dragInProgress(DragType.COMPONENT)) {
		// float[] cmpColor = selectedCmp.getColor();
		// if (cmpColor == null) {
		// cmpColor = new float[] { 1, 1, 1, 0.4f };
		// }
		// renderRange(selectedCmp, cmpColor, true);
		// if (selectedCmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.PROPULSORS
		// && selectedCmp.getTargetPosInWorld() != null) {
		// GL11.glTranslatef(-ship.getPos().x + selectedCmp.getTargetPosInWorld().x, -ship.getPos().y + selectedCmp.getTargetPosInWorld().y, 0);
		//
		// for (Component cmp : ship.getComponents().values()) {
		// if (cmp != selectedCmp && cmp.getClass().isAnnotationPresent(NeedsTarget.class)) {
		// cmpColor = cmp.getColor();
		// if (cmpColor == null) {
		// cmpColor = new float[] { 1, 1, 1, 0.4f };
		// }
		// renderRange(cmp, cmpColor, false);
		// }
		// }
		// GL11.glTranslatef(ship.getPos().x - selectedCmp.getTargetPosInWorld().x, ship.getPos().y - selectedCmp.getTargetPosInWorld().y, 0);
		// }
		// }
		//
		// if (selectedCmp != null && selectedCmp.getShip() == ship && selectedCmp.getRange() != 0) {
		// // draw component target selection
		// Vector2f targetPos = selectedCmp.getTargetPosInWorld();
		// if (targetPos != null) {
		// float radius = 20 / zoom;
		// PhysicalEntity target = selectedCmp.getTarget();
		// if (target != null) {
		// if (target instanceof Ship) {
		// radius = ((Ship) target).getMass() * 150;
		// }
		// }
		//
		// GL11.glTranslatef(targetPos.x - ship.getPos().x, targetPos.y - ship.getPos().y, 0);
		// float blur = 2 + new Random().nextFloat();
		// renderCircling(radius + blur, blur / zoom, (int) (radius / 4), new float[] { 1f, 0.5f, 0.5f, 1 });
		// GL11.glTranslatef(-targetPos.x + ship.getPos().x, -targetPos.y + ship.getPos().y, 0);
		// }
		//
		// }
		//
		// if (uiContext.getRenderMode() == RenderMode.DEBUG) {
		// // Accel
		// Vector2f accel = new Vector2f(ship.getAccel());
		// renderUtils.renderLine(Vector2f.NULL, accel, 2, 2, new float[] { 1f, 0f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		// // Speed
		// Vector2f speed = new Vector2f(ship.getSpeed());
		// renderUtils.renderLine(Vector2f.NULL, speed, 2, 2, new float[] { 0f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		//
		// renderUtils.renderLine(Vector2f.NULL, ship.debug1, 2, 2, new float[] { 0f, 0f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		// renderUtils.renderLine(Vector2f.NULL, ship.debug2, 2, 2, new float[] { 0.5f, 0.5f, 0.5f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		// renderUtils.renderLine(Vector2f.NULL, ship.debug3, 2, 2, new float[] { 1f, 1f, 1f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		// renderUtils.renderLine(Vector2f.NULL, ship.debug4, 2, 2, new float[] { 1f, 1f, 0f, 1f * alpha }, new float[] { 0f, 0f, 0f, 0f });
		// }

	}

	private void renderCircling(float radius, float blur, int nbSegments, float[] color) {
		float timeAngle;
		timeAngle = (float) (world.getAbsoluteTime() % (5000 * nbSegments)) / (5000 * nbSegments) * 360;
		GL11.glRotatef(timeAngle, 0, 0, 1);
		renderUtils.renderSegmentedCircle(
				radius,
				radius,
				blur,
				blur,
				new float[] { color[0], color[1], color[2], 0 },
				new float[] { color[0], color[1], color[2], color[3] },
				new float[] { color[0], color[1], color[2], 0 },
				nbSegments);
		renderUtils.renderAntialiasedDisc(radius, blur, new float[] { color[0], color[1], color[2], color[3] * 0.1f });
		GL11.glRotatef(-timeAngle, 0, 0, 1);
	}

	/**
	 * @param ship
	 * @param massScale
	 * @param width
	 */
	private void renderComponents(Ship ship, float alpha, FloatBuffer vpFb) {
		float zoom = uiContext.getViewport().getZoomFactor();
		final float scale = 1;

		Collection<Component> components = ship.getComponents().values();
		GL11.glScalef(scale, scale, 1);

		for (Component cmp : components) {
			Vector2f posInShip = cmp.getPosInShip();

			if (posInShip != null) {
				GL11.glTranslatef(posInShip.x, posInShip.y, zoom);
				componentRenderer.render(cmp, alpha, vpFb);
				GL11.glTranslatef(-posInShip.x, -posInShip.y, zoom);
			}
		}

		GL11.glScalef(1 / scale, 1 / scale, 1);

	}

	private void renderRange(Component selectedCmp, float[] color, boolean withLine) {
		float zoom = uiContext.getViewport().getZoomFactor();
		float blur = 2 + new Random().nextFloat();
		int nbSegments = (int) (selectedCmp.getRange() / 10);
		float timeAngle = (float) (world.getAbsoluteTime() % (5000 * nbSegments)) / (5000 * nbSegments) * 360;
		renderCircling(selectedCmp.getRange() + blur, blur / zoom, (int) (selectedCmp.getRange() / 10), color);

		if (withLine) {
			Vector2f from = new Vector2f(selectedCmp.getPosInShip()).scale(selectedCmp.getShip().getMass() / Component.SCALE)
					.rotate(selectedCmp.getShip().getRotation());
			Vector2f to = new Vector2f(0, selectedCmp.getRange() + blur).rotate(timeAngle - 90 / nbSegments);
			Vector2f vect = new Vector2f(to).sub(from);
			vect.scale((128 / Component.SCALE + 2 / zoom) / vect.length());
			from.add(vect);
			renderUtils.renderLine(from, to, 0, blur / zoom, color, new float[] { 0, 0, 0, 0 });
		}
	}

}
