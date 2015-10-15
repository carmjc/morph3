package net.carmgate.morph;

import java.nio.FloatBuffer;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.slf4j.Logger;

import net.carmgate.morph.ai.AiManager;
import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.world.WorldEventFactory;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector3f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;
import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.Window;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.particles.ParticleEngine;
import net.carmgate.morph.ui.renderers.MorphDebugDraw;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.shaders.ShaderManager;

@Singleton
public class GameMain {

	@Inject private MEventManager eventManager;
	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private MWorld world;
	@Inject private UIContext uiContext;
	@Inject private MouseManager mouseManager;
	@Inject private KeyboardManager keyboardManager;
	@Inject private WorldEventFactory worldEventFactory;
	@Inject private AiManager aiManager;
	@Inject private RenderingManager renderingManager;
	@Inject private MessageManager messageManager;
	@Inject private ComponentManager componentManager;
	@Inject private ParticleEngine particleEngine;
	@Inject private ShaderManager shaderManager;
	@Inject private MorphDebugDraw debugDraw;

	// frame counter
	private float[] frameDurations = new float[100];
	private int frameDurationIndex = 0;
	private float frameRate;
	private float instantFrameRate;

	// misc attributes
	private boolean gameLoaded;
	private int vpID;

	public void loop() {
		// init OpenGL context
		renderingManager.initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight")); //$NON-NLS-1$ //$NON-NLS-2$
		Window window = uiContext.getWindow();
		window.setWidth(Display.getWidth());
		window.setHeight(Display.getHeight());

		// init GUI
		renderingManager.initGui();

		// FIXME implement real wave management
		if (world.getShips().size() <= 1) {
			aiManager.addWave();
		}
		updateWorld();
		eventManager.scanAndRegister(world);

		messageManager.addMessage(new Message("Game Loaded"), world.getTime());

		// GL20.glUseProgram(shaderManager.getProgram("basic"));
		vpID = GL20.glGetUniformLocation(shaderManager.getProgram("basic"), "VP");

		debugDraw.init();
		debugDraw.updateWorld(1, renderingManager.getWorldVpFb(), false);
		world.getBox2dWorld().setDebugDraw(debugDraw);
		debugDraw.setFlags(DebugDraw.e_aabbBit + DebugDraw.e_centerOfMassBit + DebugDraw.e_dynamicTreeBit + DebugDraw.e_jointBit + DebugDraw.e_pairBit
				+ DebugDraw.e_shapeBit);
				// debugDraw.setFlags(DebugDraw.e_shapeBit);

		// Rendering loop
		while (true) {

			// Reset screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			// Renders everything
			if (uiContext.getRenderMode() != RenderMode.SELECT_DEBUG) {
				renderingManager.renderBackground();
				// renderingManager.renderBgParticles();
				renderingManager.renderAnimations();
				renderingManager.renderPhysical();
				// renderingManager.renderWorldAnimation();
				// renderingManager.renderFgParticles();
				renderingManager.renderGui();
			} else {
				// gameMouse.renderForSelect();
			}
			updateWorld();
			particleEngine.update();

			// Fire deferred events
			eventManager.deferredFire();


			// LOGGER.debug("stepping: " + world.getWorldMillisSinceLastBox2dUpdate());
			while (world.getWorldMillisSinceLastBox2dUpdate() >= 10) {
				// Update kinematics
				updateKinematics();
				world.getBox2dWorld().step(10f / 1000, 6, 2);
				world.setWorldMillisSinceLastBox2dUpdate(world.getWorldMillisSinceLastBox2dUpdate() - 10);
			}

			if (uiContext.getRenderMode() == RenderMode.DEBUG) {
				world.getBox2dWorld().drawDebugData();
			}

			// updates display and sets frame rate
			Display.sync(60);
			Display.update();

			// handle window resize
			if (Display.wasResized()) {
				// DOES NOT WORK
				LOGGER.debug("window resized");
				window = uiContext.getWindow();
				window.setWidth(Display.getWidth());
				window.setHeight(Display.getHeight());
			}

			Matrix4f ortho = new Matrix4f();
			ortho.setIdentity();
			float zNear = 1;
			float zFar = -1;
			ortho.m00 = 2f / window.getWidth();
			ortho.m11 = 2f / window.getHeight();
			ortho.m22 = -2f / (zFar - zNear);
			ortho.m30 = -1;
			ortho.m31 = -1;
			ortho.m32 = -(zFar + zNear) / (zFar - zNear);
			ortho.m33 = 1f;

			Matrix4f view = new Matrix4f();
			view.setIdentity();
			view.m00 = uiContext.getViewport().getZoomFactor();
			view.m11 = uiContext.getViewport().getZoomFactor();
			view.m30 = -uiContext.getViewport().getFocalPoint().x * uiContext.getViewport().getZoomFactor() + window.getWidth() / 2;
			view.m31 = uiContext.getViewport().getFocalPoint().y * uiContext.getViewport().getZoomFactor() + window.getHeight() / 2;

			Matrix4f worldVp = new Matrix4f();
			Matrix4f.mul(ortho, view, worldVp);

			// LOGGER.debug("worldVp:\n" + worldVp);
			FloatBuffer worldVpFb = renderingManager.getWorldVpFb();
			worldVp.store(worldVpFb);
			worldVpFb.flip();
			GL20.glUniformMatrix4(vpID, false, worldVpFb);

			view.setIdentity();
			view.m30 = window.getWidth() / 2;
			view.m31 = window.getHeight() / 2;

			Matrix4f guiVp = new Matrix4f();
			Matrix4f.mul(ortho, view, guiVp);

			FloatBuffer guiVpFb = renderingManager.getGuiVpFb();
			guiVp.store(guiVpFb);
			guiVpFb.flip();
			GL20.glUniformMatrix4(vpID, false, guiVpFb);

			debugDraw.updateWorld(1, renderingManager.getWorldVpFb(), false);

			// Nouveau code
			// GL11.glMatrixMode(GL11.GL_MODELVIEW);
			// GL11.glLoadIdentity();

			// Handles the window close requested event
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}

			mouseManager.handleMouseEvent();
			keyboardManager.handleKeyboardEvent();

			// Update AI
			aiManager.execute();

			// misc
			messageManager.execute(world.getTime());

			// framerate
			frameDurations[frameDurationIndex++] = world.getMillisSinceLastUpdate();
			if (frameDurationIndex == 100) {
				frameDurationIndex = 0;
			}
			float totalFramesDuration = 0;
			for (int i = 0; i < 100; i++) {
				totalFramesDuration += frameDurations[i];
			}
			frameRate = 100 / totalFramesDuration * 1000;
			if (world.getMillisSinceLastUpdate() > 0) {
				instantFrameRate = 1000 / world.getMillisSinceLastUpdate();
			} else {
				instantFrameRate = -1;
			}
			if (instantFrameRate > 0 && instantFrameRate < 30) {
				LOGGER.debug("frame rate: " + frameRate + " - instant: " + instantFrameRate);
			}
		}
	}

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		new Thread((Runnable) () -> {
			try {
				while (!GameMain.this.gameLoaded) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
						LOGGER.error("Thread.sleep interrupted", e); //$NON-NLS-1$
					}
				}
				loop();
			} catch (Throwable t) {
				LOGGER.error("Error in main game loop", t);
			} finally {
				System.exit(2);
			}
		} , "Game engine").start(); //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	private void onGameLoaded(@Observes GameLoaded gameLoaded) {
		this.gameLoaded = true;
	}

	private void updateKinematics() {
		for (final PhysicalEntity entity : world.getPhysicalEntities()) {
			//			if (entity instanceof Ship && ((Ship) entity).isForceStop()) {
			//				entity.getAccel().copy(Vec2.NULL);
			//				entity.getSpeed().copy(Vec2.NULL);
			//				((Ship) entity).setForceStop(false);
			//				continue;
			//			}
			//
			//			Vec2 tmpEntityAccel = new Vec2();
			//			Vec2 tmpAccel = new Vec2();
			//			Vec2 tmp = new Vec2();

			for (final ForceSource source : entity.getForceSources()) {
				if (source.getForce().length() > 0) {
					entity.getBody().applyForce(source.getForce(), entity.getBody().getWorldPoint(new Vec2(0, -0.1f)));
					// tmpAccel.copy(source.getForce()).scale(1 / entity.getBody().getMass()); // FIXME This is only using one force ... :(
					// tmpEntityAccel.add(tmpAccel);
				}
			}

			//
			//			// kinematics
			//			entity.getAccel().copy(tmpEntityAccel);
			//			tmp.copy(entity.getAccel()).scale((float) (world.getTime() - lastUpdateTime) / 1000);
			//			entity.getSpeed().add(tmp);
			//			tmp.copy(entity.getSpeed()).scale((float) (world.getTime() - lastUpdateTime) / 1000);
			//			entity.getPos().add(tmp);

			entity.getModelToWorld().setIdentity();
			Vec2 position = entity.getPosition();
			// LOGGER.debug("position for (" + entity + "): " + position);
			entity.getModelToWorld().translate(new Vector2f(position.x, position.y), entity.getModelToWorld());
			entity.getModelToWorld().rotate(entity.getBody().getAngle(), Vector3f.Z, entity.getModelToWorld());

			//
			//			// rotations
			//			Float rotationTarget = entity.getRotationTarget();
			//			if (rotationTarget != null) {
			//				while (rotationTarget < entity.getRotation() - 180) {
			//					rotationTarget += 360;
			//					entity.setRotationTarget(rotationTarget);
			//				}
			//				while (rotationTarget > entity.getRotation() + 180) {
			//					rotationTarget -= 360;
			//					entity.setRotationTarget(rotationTarget);
			//				}
			//
			//				if (entity.getRotation() - rotationTarget < -1) {
			//					entity.setRotationSpeed(180);
			//				} else if (entity.getRotation() - rotationTarget > 1) {
			//					entity.setRotationSpeed(-180);
			//				} else		{
			//					entity.setRotationSpeed(0);
			//					entity.setRotation(rotationTarget);
			//				}
			//			}
			//			entity.setRotation(entity.getRotation() + entity.getRotationSpeed() * world.getMillisSinceLastUpdate() / 1000);
		}
	}

	private void updateShipEconomics(final Ship ship) {

		// Compute max storage available
		float energyMax = 0;
		float resourcesMax = 0;
		for (Component cmp : ship.getComponents().values()) {
			energyMax += cmp.getMaxStoredEnergy();
			resourcesMax += cmp.getMaxStoredResources();
		}
		ship.setEnergyMax(energyMax);
		ship.setResourcesMax(resourcesMax);

		if (ship.getEnergy() > ship.getEnergyMax()) {
			ship.setEnergy(ship.getEnergyMax());
		}
		if (ship.getResources() > ship.getResourcesMax()) {
			ship.setResources(ship.getResourcesMax());
		}
		if (ship.getIntegrity() > 1) {
			ship.setIntegrity(1);
		}
	}

	private void updateWorld() {
		world.updateTime();

		for (final Ship ship : world.getShips()) {
			// Check if the ship is still alive
			if (ship.getIntegrity() <= 0) {
				final ShipDeath shipDead = worldEventFactory.newInstance(ShipDeath.class);
				shipDead.setDeadShip(ship);
				eventManager.addEvent(shipDead);
			}

			// Take into account component updates
			for (Component cmp : ship.getComponents().values()) {
				if (cmp.isActive()) {
					componentManager.evalBehavior(cmp);
				}
			}

			// economics management
			updateShipEconomics(ship);
		}
	}

}
