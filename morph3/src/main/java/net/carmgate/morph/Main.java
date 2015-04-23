package net.carmgate.morph;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.eventmgt.MEventManager;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.animations.Laser;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.inputs.KeyboardManager;
import net.carmgate.morph.ui.inputs.MouseManager;
import net.carmgate.morph.ui.renderers.LaserRenderer;
import net.carmgate.morph.ui.renderers.api.Renderable;
import net.carmgate.morph.ui.renderers.api.Renderer;
import net.carmgate.morph.ui.renderers.api.SelectRenderer;
import net.carmgate.morph.ui.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.renderers.ship.ShipRenderer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
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
   @Inject private InputHistory inputHistory;

   // Computation attributes
   private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();
   private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> selectRenderers = new HashMap<>();
   private final List<Animation> finishedAnimations = new ArrayList<>();
   private long lastUpdateTime = 0;

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

      // init the window
      // model.getWindow().setWidth(width);
      // model.getWindow().setHeight(height);

      // set clear color - Wont be needed once we have a background
      GL11.glClearColor(0f, 0f, 0f, 0f);

      // enable alpha blending
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glEnable(GL11.GL_LINE_SMOOTH);
      GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();

      GL11.glOrtho(-width / 2, width / 2, height / 2, -height / 2, 1, -1);
      GL11.glViewport(0, 0, width, height);

      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
   }
   public void loop(@Observes GameLoaded gameLoaded) {
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
         updateWorld();

         // Fire deferred events
         eventManager.deferredFire();

         // Update kinematics
         updateKinematics();

         lastUpdateTime = world.getTime();

         // updates display and sets frame rate
         Display.update();
         Display.sync(100);

         // handle window resize
         if (Display.wasResized()) {
            initView();
         }

         GL11.glMatrixMode(GL11.GL_PROJECTION);
         GL11.glLoadIdentity();

         final int width = Display.getWidth();
         final int height = Display.getHeight();
         GL11.glOrtho(-width / 2, width / 2, height / 2, -height / 2, 1, -1);
         GL11.glViewport(0, 0, width, height);

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
         ((LaserRenderer) renderers.get(anim.getClass())).render((Laser) anim);
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

      switch (uiContext.getRenderMode()) {
         case SELECT:
            renderPhysicalSelect();
            break;
         default:
            renderPhysicalNormal();
      }

      GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
      GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
   }

   private void renderPhysicalNormal() {
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
         final Vector2f pos = entity.getPos();
         GL11.glTranslatef(pos.x, pos.y, 0);
         Renderer<PhysicalEntity> renderer = (Renderer<PhysicalEntity>) renderers.get(entity.getClass());
         renderer.render(entity);
         GL11.glTranslatef(-pos.x, -pos.y, 0);
      }
   }

   private void renderPhysicalSelect() {
      final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);
      if (shipRenderer != null) {
         for (final Ship ship : world.getShips()) {
            final Vector2f pos = ship.getPos();
            GL11.glTranslatef(pos.x, pos.y, 0);
            shipRenderer.render(ship);
            GL11.glTranslatef(-pos.x, -pos.y, 0);
         }
      }
   }

   private void updateKinematics() {
      for (final PhysicalEntity entity : world.getPhysicalEntities()) {
         Vector2f tmpEntityAccel = new Vector2f();
         Vector2f tmpAccel = new Vector2f();
         Vector2f tmp = new Vector2f();

         for (final ForceSource source : entity.getForceSources()) {
            tmpAccel.copy(source.getForce()).scale(1 / entity.getMass());
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
         // move order
         if (ship.getMoveOrder() != null) {
            ship.getMoveOrder().eval();
         }

         // action order
         final Order order = ship.getCurrentOrder();
         if (order != null && !order.isDone()) {
            order.eval();
         }
         if (order != null && order.isDone()) {
            LOGGER.debug("order removed: " + order);
            ship.removeCurrentOrder();
         }

      }
   }
}
