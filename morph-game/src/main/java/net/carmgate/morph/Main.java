package net.carmgate.morph;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.physical.PhysicalEntityType;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Background;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.entities.physical.ship.components.Laser;
import net.carmgate.morph.model.entities.physical.ship.components.SimpleGenerator;
import net.carmgate.morph.model.entities.physical.ship.components.SimplePropulsor;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.orders.Order;
import net.carmgate.morph.orders.OrderFactory;
import net.carmgate.morph.orders.OrderType;
import net.carmgate.morph.orders.ship.action.Attack;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.Window;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderable;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.entities.ship.ShipRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

@Singleton
public class Main {

   @Inject private MEventManager eventManager;
   @Inject private Logger LOGGER;
   @Inject private Conf conf;
   @Inject private World world;
   @Inject private UIContext uiContext;
   @Inject private MouseManager mouseManager;
   @Inject private KeyboardManager keyboardManager;
   @Inject private PhysicalEntityFactory physicalEntityFactory;
   @Inject private OrderFactory orderFactory;

   // Computation attributes
   private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();
   private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> selectRenderers = new HashMap<>();
   private final List<Animation> finishedAnimations = new ArrayList<>();
   private long lastUpdateTime = 0;
   private static TrueTypeFont font;
   private int nextWaveId = 1;

   private boolean gameLoaded;

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
         Display.setTitle("Morph 3");
         // Display.setVSyncEnabled(true);
         Display.setResizable(true);
      } catch (final LWJGLException e) {
         e.printStackTrace();
         System.exit(0);
      }

      LOGGER.debug("init view: " + width + "x" + height);

      initView();
   }

   /**
    * Inits the view, viewport, window, etc.
    * This should be called at init and when the view changes (window is resized for instance).
    */
   private void initView() {

      final int width = Display.getWidth();
      final int height = Display.getHeight();
      LOGGER.debug("init view: " + width + "x" + height);

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
            awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("fonts/Rock_Elegance.otf"));
            awtFont = awtFont.deriveFont(12f); // set font size
            font = new TrueTypeFont(awtFont, true);
         } catch (FontFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   public void onGameLoaded(@Observes GameLoaded gameLoaded) {
      this.gameLoaded = true;
   }

   public void onContainerInitialized(@Observes ContainerInitialized containerInitializedEvent) {
      new Thread((Runnable) () -> {
         while (!Main.this.gameLoaded) {
            try {
               Thread.sleep(100);
            } catch (Exception e) {
               LOGGER.error("Thread.sleep interrupted", e);
            }
         }
         loop();
      }, "Game engine").start();
   }

   public void loop() {
      // init OpenGL context
      initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight"));

      for (final Renderer<?> renderer : renderers.values()) {
         renderer.init();
      }

      // Rendering loop
      while (true) {

         // Reset
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

         // Renders everything
         renderAnimation();
         renderPhysical();
         renderGUI();
         updateWorld();
         addWaves();

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

         // GL11.glOrtho(0, window.getWidth(), 0, -window.getHeight(), 1, -1);
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

   private void addWaves() {
      if (1 != 1 && world.getTime() > 7000 * nextWaveId * nextWaveId) {
         for (int i = 0; i < nextWaveId; i++) {
            LOGGER.debug("Adding wave " + nextWaveId);
            Ship ship = physicalEntityFactory.newInstance(PhysicalEntityType.SHIP);
            ship.getPos().copy(new Random().nextInt(1000) - 500, new Random().nextInt(800) - 400);
            ship.setPlayer(world.getPlayers().get("Other"));
            Attack attack = orderFactory.newInstance(OrderType.ATTACK, ship);
            attack.setTarget(world.getShips().get(0));
            ship.add(attack);
            ship.setMass(0.5f);
            ship.setEnergy(20);
            ship.setResources(20);
            ship.setIntegrity(1);
            ship.setDurability(5);
            ship.getComponents().put(ComponentType.LASERS, new Laser(ship));
            ship.getComponents().put(ComponentType.PROPULSORS, new SimplePropulsor(ship));
            ship.getComponents().put(ComponentType.GENERATORS, new SimpleGenerator(ship));
            world.add(ship);
         }
         nextWaveId++;
      }
   }

   private void renderGUI() {
      Ship ship = uiContext.getSelectedShip();
      int x = uiContext.getWindow().getWidth() / 2 - 2;
      int y = -uiContext.getWindow().getHeight() / 2 + 2;
      int line = 1;
      if (ship != null) {
         RenderUtils.renderText(font, x, y, MessageFormat.format("Distance: {0,number,#.###}", ship.debug1.length()), line++, Color.white, false);
         RenderUtils.renderText(font, x, y, MessageFormat.format("Speed: {0,number,#.###}", ship.getSpeed().length()), line++, Color.white, false);
         RenderUtils.renderText(font, x, y, MessageFormat.format("Accel: {0,number,#.###}", ship.getAccel().length()), line++, Color.white, false);
         RenderUtils.renderText(font, x, y, MessageFormat.format("Health: {0,number,#.#}%", ship.getIntegrity() * 100), line++, Color.white, false);
         RenderUtils.renderText(font, x, y, MessageFormat.format("Energy: {0,number,#.###}", ship.getEnergy()), line++, Color.white, false);
         RenderUtils.renderText(font, x, y, MessageFormat.format("Resources: {0,number,#.###}", ship.getResources()), line++, Color.white, false);
         if (ship.getMoveOrder() != null) {
            RenderUtils.renderText(font, x, y, MessageFormat.format("Move order: {0}", ship.getMoveOrder().getClass().getSimpleName()), line++, Color.white, false);
         }
         if (ship.getActionOrder() != null) {
            RenderUtils.renderText(font, x, y, MessageFormat.format("Action order: {0}", ship.getActionOrder().getClass().getSimpleName()), line++, Color.white, false);
         }
         if (!ship.getBgOrders().isEmpty()) {
            RenderUtils.renderText(font, x, y, "Background orders", line++, Color.white, false);
            for (Order bgOrder : ship.getBgOrders()) {
               RenderUtils.renderText(font, x, y, MessageFormat.format("{0}", bgOrder.getClass().getSimpleName()), line++, Color.white, false);
            }
         }
         if (uiContext.getRenderMode() == RenderMode.DEBUG) {
            for (Component c : ship.getComponents().values()) {
               Color color = Color.white;
               if (!c.isActive()) {
                  color = Color.red;
               }
               RenderUtils.renderText(font, x, y,
                     MessageFormat.format(c.getClass().getSimpleName() + ": {0,number,#.###}/{1,number,#.###}", c.getEnergyDt(), c.getResourcesDt()), line++, color, false);
            }
         }
      }
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
                  LOGGER.debug("Added new renderer: " + renderer.getClass().getName() + " for " + type.getName());
               }
               if (paramType.getRawType().equals(SelectRenderer.class)) {
                  final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
                  selectRenderers.put(type, renderer);
                  LOGGER.debug("Added new selectRenderer: " + renderer.getClass().getName() + " for " + type.getName());
               }
            }
         }
      } catch (final Exception e) {
         LOGGER.error("Error", e);
      }
   }

   private void renderAnimation() {
      final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
      final float zoomFactor = uiContext.getViewport().getZoomFactor();
      GL11.glScalef(zoomFactor, zoomFactor, 1);
      GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);
      world.getAnimations().forEach(anim -> {
         Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(anim.getClass());
         renderer.render(anim);
         if (anim.getAnimationEnd() < world.getTime()) {
            finishedAnimations.add(anim);
         }
      });
      finishedAnimations.forEach(a -> {
         world.remove(a);
      });
      finishedAnimations.clear();
      GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
      GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
   }

   private void renderPhysical() {
      final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
      final float zoomFactor = uiContext.getViewport().getZoomFactor();
      GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);
      GL11.glScalef(zoomFactor, zoomFactor, 1);

      final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);
      if (shipRenderer != null) {
         for (final Ship ship : world.getShips()) {
            final Vector2f pos = ship.getPos();
            GL11.glTranslatef(pos.x, pos.y, 0);
            shipRenderer.render(ship);
            GL11.glTranslatef(-pos.x, -pos.y, 0);
         }
      }

      for (PhysicalEntity entity : world.getPhysicalEntities()) {
         if (!(entity instanceof Ship)) {
            final Vector2f pos = entity.getPos();
            GL11.glTranslatef(pos.x, pos.y, 0);
            Renderer<PhysicalEntity> renderer = (Renderer<PhysicalEntity>) renderers.get(entity.getClass());
            renderer.render(entity);
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

   private void updateWorld() {
      world.updateTime();
      for (final Ship ship : world.getShips()) {
         // economics management
         updateShipEconomics(ship);

         // move order
         if (ship.getMoveOrder() != null) {
            ship.getMoveOrder().eval();
         }

         // action order
         final Order order = ship.getActionOrder();
         if (order != null && !order.isDone()) {
            order.eval();
         }
         if (order != null && order.isDone()) {
            LOGGER.debug("order removed: " + order);
            ship.removeActionOrder();
         }

         // background orders
         List<Order> bgOrdersToRemove = new ArrayList<>();
         for (Order bgOrder : ship.getBgOrders()) {
            if (bgOrder != null && !bgOrder.isDone()) {
               bgOrder.eval();
            }
            if (bgOrder != null && bgOrder.isDone()) {
               LOGGER.debug("order removed: " + bgOrder);
               bgOrdersToRemove.add(bgOrder);
            }
         }
         ship.getBgOrders().removeAll(bgOrdersToRemove);
      }
   }

   private void updateShipEconomics(final Ship ship) {
      // Economics updates from components
      Map<ComponentType, Integer> componentCriticities = new HashMap<>();
      if (ship.getMoveOrder() != null) {
         for (ComponentType compType : ship.getMoveOrder().getComponentTypes()) {
            if (componentCriticities.get(compType) == null || componentCriticities.get(compType) > ship.getMoveOrder().getCriticity()) {
               componentCriticities.put(compType, ship.getMoveOrder().getCriticity());
            }
         }
      }
      if (ship.getActionOrder() != null) {
         for (ComponentType compType : ship.getActionOrder().getComponentTypes()) {
            if (componentCriticities.get(compType) == null || componentCriticities.get(compType) > ship.getActionOrder().getCriticity()) {
               componentCriticities.put(compType, ship.getActionOrder().getCriticity());
            }
         }
      }
      for (Entry<ComponentType, Component> entry : ship.getComponents().entrySet()) {
         if (entry.getValue().getClass().isAnnotationPresent(Background.class)) {
            componentCriticities.put(entry.getKey(), 0);
         }
      }

      Map<Integer, Set<ComponentType>> map = new TreeMap<>((o1, o2) -> o2 - o1);
      for (Map.Entry<ComponentType, Integer> entry : componentCriticities.entrySet()) {
         Set<ComponentType> set = map.get(entry.getValue());
         if (set == null) {
            set = new HashSet<>();
            map.put(entry.getValue(), set);
         }
         set.add(entry.getKey());
      }

      ship.setEnergyDt(0);
      ship.setResourcesDt(0);
      for (Set<ComponentType> set : map.values()) {
         for (ComponentType cp : set) {
            if (ship.getEnergy() + ship.getEnergyDt() + ship.getComponents().get(cp).getEnergyDt() >= 0 &&
                  ship.getResources() + ship.getResourcesDt() + ship.getComponents().get(cp).getResourcesDt() >= 0) {
               ship.getComponents().get(cp).setActive(true);
               ship.setEnergyDt(ship.getEnergyDt() + ship.getComponents().get(cp).getEnergyDt());
               ship.setResourcesDt(ship.getResourcesDt() + ship.getComponents().get(cp).getResourcesDt());
            } else {
               ship.getComponents().get(cp).setActive(false);
            }
         }
      }

      // Energy and resources evolution with time
      float energyDelta = ship.getEnergyDt() * (world.getTime() - lastUpdateTime) / 1000;
      if (ship.getEnergy() + energyDelta < 0) {
         ship.setEnergy(0);
      } else {
         ship.addEnergy(energyDelta);
      }
      float resourcesDelta = ship.getResourcesDt() * (world.getTime() - lastUpdateTime) / 1000;
      if (ship.getResources() + resourcesDelta < 0) {
         ship.setResources(0);
      } else {
         ship.addResources(resourcesDelta);
      }
   }
}
