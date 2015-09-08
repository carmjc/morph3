package net.carmgate.morph;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.physical.PhysicalEntityType;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentFactory;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.entities.physical.ship.components.Laser;
import net.carmgate.morph.model.entities.physical.ship.components.SimplePropulsor;
import net.carmgate.morph.model.entities.physical.ship.components.SolarPanelGenerator;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.Window;
import net.carmgate.morph.ui.actions.Select;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderable;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.entities.ship.ShipRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.WidgetContainer;
import net.carmgate.morph.ui.widgets.WidgetFactory;

@Singleton
public class GameMain {

	private static TrueTypeFont font;
	@Inject private MEventManager eventManager;
	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private World world;
	@Inject private UIContext uiContext;
	@Inject private MouseManager mouseManager;
	@Inject private KeyboardManager keyboardManager;
	@Inject private PhysicalEntityFactory physicalEntityFactory;
	// @Inject private OrderFactory orderFactory;
	@Inject private ComponentFactory componentFactory;
	@Inject private Messages messages;
	@Inject private WidgetFactory widgetFactory;

	@Inject private Select select;
	// Computation attributes
	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();
	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> selectRenderers = new HashMap<>();
	private long lastUpdateTime = 0;
	private int nextWaveId = 1;

	private boolean gameLoaded;

	// TODO Find an other way to do this
	@Deprecated
	private void addWaves() {
		if (world.getTime() > 7000 * nextWaveId * nextWaveId) {
			for (int i = 0; i < nextWaveId; i++) {
				LOGGER.debug("Adding wave " + nextWaveId); //$NON-NLS-1$
				Ship ship = physicalEntityFactory.newInstance(PhysicalEntityType.SHIP);
				ship.getPos().copy(new Random().nextInt(1000) - 500, new Random().nextInt(800) - 400);
				ship.setPlayer(world.getPlayers().get("Other")); //$NON-NLS-1$
				// Attack attack = orderFactory.newInstance(OrderType.ATTACK, ship);
				// attack.setTarget(world.getShips().get(0));
				// ship.add(attack);
				ship.setMass(0.5f);
				ship.setEnergy(20);
				ship.setResources(20);
				ship.setIntegrity(1);
				ship.setDurability(5);
				ship.add(componentFactory.newInstance(Laser.class), 1f / 8);
				ship.add(componentFactory.newInstance(SimplePropulsor.class), 3f / 4);
				ship.add(componentFactory.newInstance(SolarPanelGenerator.class), 1f / 8);
				world.add(ship);
			}
			nextWaveId++;
		}
	}

	/**
	 * Initialise the GL display
	 *
	 * @param width
	 *           The width of the display
	 * @param height
	 *           The height of the display
	 */
	private void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setTitle(conf.getProperty("ui.window.title")); //$NON-NLS-1$
			// Display.setVSyncEnabled(true);
			Display.setResizable(true);
		} catch (final LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		LOGGER.debug("init view: " + width + "x" + height); //$NON-NLS-1$ //$NON-NLS-2$

		initView();
	}

	private void initGui() {
		uiContext.setWidgetRoot(widgetFactory.newInstance(WidgetContainer.class));
		// ComponentPonderationWidget componentPonderationWidget = widgetFactory.newInstance(ComponentPonderationWidget.class);
		// componentPonderationWidget.setPosition(new float[] { 5, uiContext.getWindow().getHeight() - 50 });
		// uiContext.getWidgetRoot().add(componentPonderationWidget);
	}

	/**
	 * Inits the view, viewport, window, etc.
	 * This should be called at init and when the view changes (window is resized for instance).
	 */
	private void initView() {

		final int width = Display.getWidth();
		final int height = Display.getHeight();
		LOGGER.debug("init view: " + width + "x" + height); //$NON-NLS-1$ //$NON-NLS-2$

		// set clear color - Wont be needed once we have a background
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 0f);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(-width / 2, width / 2, height / 2, -height / 2, 1, -1);
		GL11.glViewport(0, 0, width, height);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if (font == null) {
			// Font awtFont = new Font("Verdana", Font.PLAIN, 11);
			Font awtFont;
			try {
				awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(conf.getProperty("ui.font"))); //$NON-NLS-1$
				awtFont = awtFont.deriveFont(conf.getFloatProperty("ui.font.size")); // set font size //$NON-NLS-1$
				font = new TrueTypeFont(awtFont, true);
			} catch (FontFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void loop() {
		// init OpenGL context
		initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight")); //$NON-NLS-1$ //$NON-NLS-2$

		// init GUI
		initGui();

		for (final Renderer<?> renderer : renderers.values()) {
			renderer.init();
		}

		// Rendering loop
		while (true) {

			// Reset screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			// Renders everything
			if (uiContext.getRenderMode() != RenderMode.SELECT_DEBUG) {
				renderComponentsAnimation();
				renderPhysical();
				renderGui();
			} else {
				select.renderForSelect();
			}
			updateWorld();
			// addWaves();

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
				initView();
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
		}
	}

	@SuppressWarnings("unused")
	private void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
		new Thread((Runnable) () -> {
			while (!GameMain.this.gameLoaded) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					LOGGER.error("Thread.sleep interrupted", e); //$NON-NLS-1$
				}
			}
			loop();
		}, "Game engine").start(); //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	private void onGameLoaded(@Observes GameLoaded gameLoaded) {
		this.gameLoaded = true;
	}

	@SuppressWarnings({ "unused" })
	private void registerRenderer(@Observes NewRendererFound event) {
		try {
			final Renderer<? extends Renderable> renderer = event.getRenderer();
			final Type[] interfaces = renderer.getClass().getGenericInterfaces();
			for (final Type interf : interfaces) {
				if (interf instanceof ParameterizedType) {
					final ParameterizedType paramType = (ParameterizedType) interf;
					if (paramType.getRawType().equals(Renderer.class)) {
						final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
						renderers.put(type, renderer);
						LOGGER.debug("Added new renderer: " + renderer.getClass().getName() + " for " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (paramType.getRawType().equals(SelectRenderer.class)) {
						final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
						selectRenderers.put(type, renderer);
						LOGGER.debug("Added new selectRenderer: " + renderer.getClass().getName() + " for " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		} catch (final Exception e) {
			LOGGER.error("Error", e); //$NON-NLS-1$
		}
	}

	private void renderComponentsAnimation() {
		for (final Ship ship : world.getShips()) {
			final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
			final float zoomFactor = uiContext.getViewport().getZoomFactor();
			GL11.glScalef(zoomFactor, zoomFactor, 1);
			GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);

			Collection<Component> components = ship.getComponents().values();
			components.forEach(cmp -> {
				if (cmp.isActive() && !cmp.isFamished() && !cmp.isUseless()) {
					Animation anim = cmp.getAnimation();
					if (anim != null) {
						Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(anim.getClass());
						if (anim.getAnimationEnd() > world.getTime()) {
							renderer.render(anim);
						}
						if (anim.getAnimationEnd() + anim.getAnimationCoolDown() < world.getTime()) {
							anim.setAnimationEnd(anim.getAnimationEnd() + anim.getAnimationCoolDown() + anim.getAnimationDuration());
						}
					}
				}
			});

			GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
			GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
		}
	}

	private void renderGui() {
		renderGuiForSelectedShip();

		float x = uiContext.getWindow().getWidth() / 2 - 2;
		float y = uiContext.getWindow().getHeight() / 2 - 2;
		int line = 0;
		if (world.isTimeFrozen()) {
			RenderUtils.renderText(font, x, y, messages.getString("ui.game.paused"), line--, Color.white, false); //$NON-NLS-1$
		}
	}

	private void renderGuiForSelectedShip() {
		Ship ship = uiContext.getSelectedShip();
		float borderLeftX = uiContext.getWindow().getWidth() / 2 - 2;
		float borderTopY = -uiContext.getWindow().getHeight() / 2 + 2;
		int line = 1;
		if (ship != null) {
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.distance"), ship.debug1.length()), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.speed"), ship.getSpeed().length()), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.accel"), ship.getAccel().length()), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.health"), ship.getIntegrity() * 100), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.eco"), ship.getEnergy(), ship.getResources()), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.ecoDt"), ship.getEnergyDt(), ship.getResourcesDt()), line++, Color.white, false); //$NON-NLS-1$
			RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.ecoMax"), ship.getEnergyMax(), ship.getResourcesMax()), line++, Color.white, false); //$NON-NLS-1$
			// if (ship.getMoveOrder() != null) {
			// RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.moveOrder"),
			// ship.getMoveOrder().getClass().getSimpleName()), line++, Color.white, false); //$NON-NLS-1$
			// }
			// if (ship.getActionOrder() != null) {
			// RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.actionOrder"),
			// ship.getActionOrder().getClass().getSimpleName()), line++, Color.white, false); //$NON-NLS-1$
			// }
			// if (!ship.getBgOrders().isEmpty()) {
			// RenderUtils.renderText(font, borderLeftX, borderTopY, messages.getString("ui.selectedShip.backgroundOrders"), line++, Color.white, false);
			// //$NON-NLS-1$
			// for (Order bgOrder : ship.getBgOrders()) {
			// RenderUtils.renderText(font, borderLeftX, borderTopY, MessageFormat.format(messages.getString("ui.selectedShip.backgroundOrder"),
			// bgOrder.getClass().getSimpleName()), line++, Color.white, false); //$NON-NLS-1$
			// }
			// }
			if (uiContext.getRenderMode() == RenderMode.DEBUG) {
				for (Component c : ship.getComponents().values()) {
					Color color = Color.white;
					if (c.isFamished()) {
						color = Color.red;
					}
					if (!c.isActive() || c.isUseless()) {
						color = Color.gray;
					}

					GL11.glTranslatef(borderLeftX - 5, borderTopY + font.getLineHeight() * line - 10, 0);
					ComponentType cmpType = c.getClass().getAnnotation(ComponentKind.class).value();
					float[] cmpColor = cmpType.getColor();
					GL11.glColor3f(cmpColor[0], cmpColor[1], cmpColor[2]);
					RenderUtils.renderQuad(0, 0, 5, 5);
					GL11.glTranslatef(-(borderLeftX - 5), -(borderTopY + font.getLineHeight() * line - 10), 0);

					float energyDt = c.isUseless() ? 0 : c.getEnergyDt();
					float resourcesDt = c.isUseless() ? 0 : c.getResourcesDt();
					RenderUtils.renderText(font, borderLeftX - 10, borderTopY,
							MessageFormat.format(messages.getString("ui.selectedShip.components"), c.getClass().getSimpleName(), energyDt, resourcesDt), line++, color, false); //$NON-NLS-1$

				}
			}
		}

		float borderRightX = -uiContext.getWindow().getWidth() / 2;
		borderTopY = -uiContext.getWindow().getHeight() / 2;

		GL11.glTranslatef(borderRightX, borderTopY, 0);
		uiContext.getWidgetRoot().renderWidget();
		GL11.glTranslatef(-borderRightX, -borderTopY, 0);
	}

	private void renderPhysical() {
		final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
		final float zoomFactor = uiContext.getViewport().getZoomFactor();
		GL11.glScalef(zoomFactor, zoomFactor, 1);
		GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);

		for (PhysicalEntity entity : world.getPhysicalEntities()) {
			if (!(entity instanceof Ship)) {
				final Vector2f pos = entity.getPos();
				GL11.glTranslatef(pos.x, pos.y, 0);
				Renderer<PhysicalEntity> renderer = (Renderer<PhysicalEntity>) renderers.get(entity.getClass());
				renderer.render(entity);
				GL11.glTranslatef(-pos.x, -pos.y, 0);
			}
		}

		final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);
		if (shipRenderer != null) {
			for (final Ship ship : world.getShips()) {
				final Vector2f pos = ship.getPos();
				GL11.glTranslatef(pos.x, pos.y, 0);
				shipRenderer.render(ship);
				GL11.glTranslatef(-pos.x, -pos.y, 0);
			}
		}

		GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
		GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
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
			// entity.getSpeed().scale(1f - 0.005f * (world.getTime() - lastUpdateTime) / 1000); // drag
			tmp.copy(entity.getSpeed()).scale((float) (world.getTime() - lastUpdateTime) / 1000);
			entity.getPos().add(tmp);

			// rotations
			entity.setRotate(entity.getRotate() + entity.getRotateSpeed() * (world.getTime() - lastUpdateTime) / 1000);
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

		// adjust stored amounts so that we do not have epsilon vibrations of stored amounts
		// if (ship.getEnergy() * 0.99f > ship.getEnergyMax() && ship.getEnergyMax() > 0) {
		// ship.setEnergy(ship.getEnergyMax());
		// }
		// if (ship.getResources() * 0.99f > ship.getResourcesMax() && ship.getResourcesMax() > 0) {
		// ship.setResources(ship.getResourcesMax());
		// }

		// Energy and resources evolution with time
		// float energyDelta = ship.getEnergyDt() * (world.getTime() - lastUpdateTime) / 1000;
		// if (ship.getEnergy() + energyDelta < 0) {
		// ship.setEnergy(0);
		// } else {
		// ship.setEnergy(Math.min(ship.getEnergy() + energyDelta, energyMax));
		// }
		// float resourcesDelta = ship.getResourcesDt() * (world.getTime() - lastUpdateTime) / 1000;
		// if (ship.getResources() + resourcesDelta < 0) {
		// ship.setResources(0);
		// } else {
		// ship.setResources(Math.min(ship.getResources() + resourcesDelta, resourcesMax));
		// }
		// float integrityDelta = ship.getIntegrityDt() * (world.getTime() - lastUpdateTime) / 1000;
		// if (ship.getIntegrity() + integrityDelta < 0) {
		// ship.setIntegrity(0);
		// } else {
		// ship.setIntegrity(ship.getIntegrity() + integrityDelta);
		// }
	}

	private void updateWorld() {
		world.updateTime();
		for (final Ship ship : world.getShips()) {
			// Take into account component updates
			for (Component cmp : ship.getComponents().values()) {
				if (cmp.isActive()) {
					cmp.evalBehavior();
				}
			}

			// economics management
			updateShipEconomics(ship);

			// // move order
			// if (ship.getMoveOrder() != null) {
			// ship.getMoveOrder().eval();
			// }
			//
			// // action order
			// final Order order = ship.getActionOrder();
			// if (order != null && !order.isDone()) {
			// order.eval();
			// }
			// if (order != null && order.isDone()) {
			// LOGGER.debug("order removed: " + order); //$NON-NLS-1$
			// ship.removeActionOrder();
			// }
			//
			// // background orders
			// List<Order> bgOrdersToRemove = new ArrayList<>();
			// for (Order bgOrder : ship.getBgOrders()) {
			// if (bgOrder != null && !bgOrder.isDone()) {
			// bgOrder.eval();
			// }
			// if (bgOrder != null && bgOrder.isDone()) {
			// LOGGER.debug("order removed: " + bgOrder); //$NON-NLS-1$
			// bgOrdersToRemove.add(bgOrder);
			// }
			// }
			// ship.getBgOrders().removeAll(bgOrdersToRemove);
		}
	}

}
