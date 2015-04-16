package net.carmgate.morph;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.Model;
import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.renderers.RenderMode;
import net.carmgate.morph.model.renderers.Renderable;
import net.carmgate.morph.model.renderers.Renderer;
import net.carmgate.morph.model.renderers.ShipRenderer;
import net.carmgate.morph.model.renderers.events.NewRendererFound;
import net.carmgate.morph.ui.UIContext;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.slf4j.Logger;

@Singleton
public class Main {

   @Inject
   private Logger LOGGER;

   @Inject
   private Conf conf;

   @Inject
   private Model model;

   @Inject
   private UIContext uiContext;

   private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();

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

   /**
    * Main Class
    */
   public void start(@Observes ContainerInitialized event) {
      LOGGER.debug("received in main");
      try {
         main();
      } catch (Exception e) {
         LOGGER.error("main exception", e);
      }
   }

   @SuppressWarnings({ "unused" })
   private void registerRenderer(@Observes NewRendererFound event) {
      try {
         final Renderer<? extends Renderable> renderer = event.getRenderer();
         LOGGER.debug("Trying to add new renderer: " + renderer.getClass().getName());
         final Type[] interfaces = renderer.getClass().getGenericInterfaces();
         for (final Type interf : interfaces) {
            if (interf instanceof ParameterizedType) {
               final ParameterizedType paramType = (ParameterizedType) interf;
               if (paramType.getRawType().equals(Renderer.class)) {
                  final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
                  renderers.put(type, renderer);
                  LOGGER.debug("Added new renderer: " + renderer.getClass().getName() + " for " + type.getName());
               }
            }
         }
      } catch (final Exception e) {
         LOGGER.error("Error", e);
      }
   }

   private void render() {
      final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);

      if (uiContext.getRenderMode() == RenderMode.NORMAL) {
         if (shipRenderer != null) {
            for (final Ship ship : model.getShips()) {
               Vector2f pos = ship.getPos();
               GL11.glTranslatef(pos.x, pos.y, 0);
               shipRenderer.render(ship);
               GL11.glTranslatef(-pos.x, -pos.y, 0);
            }
         }
      }
   }

   /**
    * Start the application
    */
   public void main() {
      // init OpenGL context
      initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight"));

      for (final Renderer renderer : renderers.values()) {
         renderer.init();
      }

      // Rendering loop
      while (true) {

         // Renders everything
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
         if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            render();
         } else {
            // new WorldSelect().render(GL11.GL_SELECT);
         }

         // updates display and sets frame rate
         Display.update();
         Display.sync(200);

         // update model
         // Model.getModel().update();

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
      }
   }
}
