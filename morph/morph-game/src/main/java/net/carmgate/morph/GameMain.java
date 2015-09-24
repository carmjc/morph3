package net.carmgate.morph;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import net.carmgate.morph.ai.AiManager;
import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.world.WorldEventFactory;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;
import net.carmgate.morph.ui.RenderingManager;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.Window;
import net.carmgate.morph.ui.inputs.GameMouse;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.particles.ParticleEngine;
import net.carmgate.morph.ui.renderers.RenderMode;

@Singleton
public class GameMain {

	@Inject private MEventManager eventManager;
	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private World world;
	@Inject private UIContext uiContext;
	@Inject private MouseManager mouseManager;
	@Inject private KeyboardManager keyboardManager;
	@Inject private WorldEventFactory worldEventFactory;
	@Inject private GameMouse gameMouse;
	@Inject private AiManager aiManager;
	@Inject private RenderingManager renderingManager;
	@Inject private MessageManager messageManager;
	@Inject private EntityManager em;
	@Inject private ComponentManager componentManager;
	@Inject private ParticleEngine particleEngine;

	// Computation attributes
	private long lastUpdateTime = 0;

	// misc attributes
	private boolean gameLoaded;

	private void initDb() {
		LOGGER.debug("em: " + em);
	}

	public void loop() {
		// init OpenGL context
		renderingManager.initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight")); //$NON-NLS-1$ //$NON-NLS-2$

		// init GUI
		renderingManager.initGui();

		// init db
		initDb();

		// FIXME implement real wave management
		if (world.getShips().size() <= 1) {
			aiManager.addWave();
		}
		updateWorld();
		eventManager.scanAndRegister(world);

		messageManager.addMessage(new Message("Game Loaded"), world.getTime());

		// Rendering loop
		while (true) {

			// Reset screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			// Renders everything
			if (uiContext.getRenderMode() != RenderMode.SELECT_DEBUG) {
				renderingManager.renderBackground();
				renderingManager.renderBgParticles();
				renderingManager.renderComponentsAnimation();
				renderingManager.renderPhysical();
				renderingManager.renderWorldAnimation();
				renderingManager.renderFgParticles();
				renderingManager.renderGui();
			} else {
				gameMouse.renderForSelect();
			}
			updateWorld();
			particleEngine.update();

			// Fire deferred events
			eventManager.deferredFire();

			// Update kinematics
			updateKinematics();

			lastUpdateTime = world.getTime();

			// updates display and sets frame rate
			Display.update();
			Display.sync(100);

			// handle window resize
			Window window = uiContext.getWindow();
			if (Display.wasResized()) {
				renderingManager.initView();
				window.setWidth(Display.getWidth());
				window.setHeight(Display.getHeight());
			}

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();

			// GL11.glOrtho(-window.getWidth() / 2, window.getWidth(), window.getHeight() / 2, -window.getHeight(), 1, -1);
			GL11.glOrtho(-window.getWidth() / 2, window.getWidth() / 2, window.getHeight() / 2, -window.getHeight() / 2, 1, -1);
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

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
			if (entity instanceof Ship && ((Ship) entity).isForceStop()) {
				entity.getAccel().copy(Vector2f.NULL);
				entity.getSpeed().copy(Vector2f.NULL);
				((Ship) entity).setForceStop(false);
				continue;
			}

			Vector2f tmpEntityAccel = new Vector2f();
			Vector2f tmpAccel = new Vector2f();
			Vector2f tmp = new Vector2f();

			for (final ForceSource source : entity.getForceSources()) {
				tmpAccel.copy(source.getForce()).scale(1 / entity.getMass()); // FIXME This is only using one force ... :(
				tmpEntityAccel.add(tmpAccel);
			}

			// kinematics
			entity.getAccel().copy(tmpEntityAccel);
			tmp.copy(entity.getAccel()).scale((float) (world.getTime() - lastUpdateTime) / 1000);
			entity.getSpeed().add(tmp);
			tmp.copy(entity.getSpeed()).scale((float) (world.getTime() - lastUpdateTime) / 1000);
			entity.getPos().add(tmp);

			// rotations
			Float rotationTarget = entity.getRotationTarget();
			if (rotationTarget != null) {
				while (rotationTarget < entity.getRotation() - 180) {
					rotationTarget += 360;
					entity.setRotationTarget(rotationTarget);
				}
				while (rotationTarget > entity.getRotation() + 180) {
					rotationTarget -= 360;
					entity.setRotationTarget(rotationTarget);
				}

				if (entity.getRotation() - rotationTarget < -1) {
					entity.setRotationSpeed(180);
				} else if (entity.getRotation() - rotationTarget > 1) {
					entity.setRotationSpeed(-180);
				} else		{
					entity.setRotationSpeed(0);
					entity.setRotation(rotationTarget);
				}
				entity.setRotation(entity.getRotation() + entity.getRotationSpeed() * (world.getTime() - lastUpdateTime) / 1000);
			}
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
