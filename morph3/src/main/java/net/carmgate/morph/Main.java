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
import net.carmgate.morph.model.renderers.events.NewRenderer;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Main {

	private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	@Inject private Conf conf;

	@Inject private Model model;

	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();

	/**
	 * Initialise the GL display
	 *
	 * @param width The width of the display
	 * @param height The height of the display
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

	//	private int fpsCounter = 0;
	//	private float fps = 0;
	//	private long lastFpsResetTs = 0;
	//
	//	private ShipEditorLayer shipEditorLayer;
	//	private NormalLayer normalLayer;

	/**
	 * Inits the view, viewport, window, etc.
	 * This should be called at init and when the view changes (window is resized for instance).
	 */
	private void initView() {

		final int width = Display.getWidth();
		final int height = Display.getHeight();
		LOGGER.debug("init view: " + width + "x" + height);

		// init the window
		//		model.getWindow().setWidth(width);
		//		model.getWindow().setHeight(height);

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
	 * Scans the classpath looking for renderers (classes annotated with {@link Renderable})
	 * initializes them and add them to the maps of the renderers
	 */
	//	private void initRenderables() {
	//		// Init the reflection API
	//		// Look for classes implementing the Renderable interface
	//		Set<Class<? extends Renderable>> renderables = new Reflections("net.carmgate.morph").getSubTypesOf(Renderable.class);
	//
	//		// Iterate over the result set to initialize each renderable
	//		for (Class<? extends Renderable> renderable : renderables) {
	//			// if the class is abstract, do not try to instanciate
	//			if (Modifier.isAbstract(renderable.getModifiers())) {
	//				// if the initRenderer method is not abstract in this class, log an error
	//				try {
	//					if (!Modifier.isAbstract(renderable.getMethod("initRenderer", new Class<?>[] {}).getModifiers())) {
	//						LOGGER.error("There is a concrete initRenderer method in abstract class "
	//								+ renderable.getCanonicalName() + ". Check the class as it might be an error.");
	//					}
	//				} catch (NoSuchMethodException | SecurityException e) {
	//					LOGGER.error("Error while retrieveing initRenderer method within " + renderable.getName(), e);
	//				}
	//				continue;
	//			}
	//
	//			// If the class is not abstract, call the no-arg constructor and then the initRenderer method
	//			try {
	//				Constructor<? extends Renderable> constructor = renderable.getConstructor();
	//				constructor.newInstance().initRenderer();
	//			} catch (InstantiationException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
	//					| InvocationTargetException e) {
	//				LOGGER.error("Exception raised while trying to init renderer " + renderable.getName(), e);
	//			}
	//
	//		}
	//	}

	/**
	 * Main Class
	 */
	public void main(@Observes ContainerInitialized event) {
		LOGGER.debug("received in main");
		start();
	}

	/**
	 * Render the view.
	 */
	//	public void render() {
	//
	//		try {
	//
	//			switch (Model.getModel().getUiContext().getUiState()) {
	//			case NORMAL:
	//				normalLayer.render(GL11.GL_RENDER);
	//				break;
	//			case SHIP_EDITOR:
	//				normalLayer.render(GL11.GL_RENDER);
	//				shipEditorLayer.render(GL11.GL_RENDER);
	//				break;
	//			}
	//
	//			RenderUtils.renderLineToConsole("FPS: " + fps, 1);
	//
	//		} catch (Exception e) {
	//			LOGGER.debug("Exception caught in main loop.", e);
	//		}
	//	}

	/**
	 * Run an action, if the current {@link UIState} matches the {@link UIState} defined for the action.
	 */
	//	private void runAction(Action action) {
	//		ActionHints actionHints = action.getClass().getAnnotation(ActionHints.class);
	//		for (UIState uiState : actionHints.uiState()) {
	//			if (uiState == Model.getModel().getUiContext().getUiState()) {
	//				action.run();
	//				break;
	//			}
	//		}
	//	}

	/**
	 * This method initializes UI handlers.
	 * Some special case handlers can not be initialized dynamically at the moment.
	 */
	//	private void initActions() {
	//		// The drag context shared by all actions needing to handle drag
	//		DragContext dragContext = new DragContext();
	//
	//		// select actions having to be handled before anything else
	//		// because some other actions (like MoveTo or Attack) need the result of the action selection
	//		mouseActions.add(new WorldSelect());
	//		mouseActions.add(new WorldMultiSelect());
	//		mouseActions.add(new ShipEditorSelect());
	//
	//		// Look for the action classes
	//		Set<Class<? extends Action>> actions = new Reflections("net.carmgate.morph.actions").getSubTypesOf(Action.class);
	//		for (Class<? extends Action> action : actions) {
	//			try {
	//				// Instanciate the action
	//				Action actionInstance = action.newInstance();
	//
	//				// Handle actions hints
	//				// Instanciate drag actions with common drag context
	//				if (action.getAnnotation(ActionHints.class).dragAction()) {
	//					// Get the fields of type DragContext
	//					Set<Field> fields = ReflectionUtils.getFields(action, new Predicate<Field>() {
	//						@Override
	//						public boolean apply(Field input) {
	//							return input.getType().equals(DragContext.class);
	//						}
	//					});
	//
	//					// set the DragContext fields
	//					for (Field field : fields) {
	//						boolean accessible = field.isAccessible();
	//						if (!accessible) {
	//							field.setAccessible(true);
	//						}
	//						field.set(actionInstance, dragContext);
	//						if (!accessible) {
	//							field.setAccessible(false);
	//						}
	//					}
	//				}
	//
	//				// autoload mouse actions if requested
	//				if (action.getAnnotation(ActionHints.class).mouseActionAutoload()) {
	//					mouseActions.add(actionInstance);
	//				}
	//
	//				// autoload keyboard actions if requested
	//				if (action.getAnnotation(ActionHints.class).keyboardActionAutoload()) {
	//					keyboardActions.add(actionInstance);
	//				}
	//			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
	//				LOGGER.error("Exception raised while creating actions", e);
	//			}
	//
	//		}
	//	}

	@SuppressWarnings({ "unused" })
	private void registerRenderer(@Observes NewRenderer event) {
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

		if (shipRenderer != null) {
			for (final Ship ship : model.getShips()) {
				shipRenderer.render(ship, RenderMode.NORMAL);
			}
		}
	}

	/**
	 * Start the application
	 */
	public void start() {
		// init OpenGL context
		initGL(conf.getIntProperty("window.initialWidth"), conf.getIntProperty("window.initialHeight"));

		for (final Renderer renderer : renderers.values()) {
			renderer.init();
		}

		//
		//		// init the layers
		//		shipEditorLayer = new ShipEditorLayer();
		//		shipEditorLayer.setShip(Model.getModel().getSelfShip());
		//		normalLayer = new NormalLayer();
		//
		//		// Configure Actions
		//		initActions();
		//
		//		// TODO this should be in a UI renderer
		//		Font awtFont = new Font("Tahoma", Font.BOLD, 14);
		//		RenderUtils.font = new TrueTypeFont(awtFont, true);

		// Rendering loop
		while (true) {

			// Renders everything
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				render();
			} else {
				//				new WorldSelect().render(GL11.GL_SELECT);
			}

			// updates display and sets frame rate
			Display.update();
			Display.sync(200);

			// update model
			//			Model.getModel().update();

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

			// Fire events accordingly
			//			if (Mouse.next()) {
			//				int dWheel = Mouse.getDWheel();
			//				if (dWheel != 0) {
			//					LOGGER.debug("Logged a mouse wheel: " + dWheel);
			//					Model.getModel().getInteractionStack()
			//					.addEvent(new UIEvent(EventType.MOUSE_WHEEL, dWheel, new int[] { Mouse.getEventX(), Mouse.getEventY() }));
			//					for (Action action : mouseActions) {
			//						runAction(action);
			//					}
			//				}
			//
			//				// add interaction to ui context
			//				EventType evtType = null;
			//				if (Mouse.getEventButton() >= 0) {
			//					if (Mouse.getEventButtonState()) {
			//						evtType = EventType.MOUSE_BUTTON_DOWN;
			//					} else {
			//						evtType = EventType.MOUSE_BUTTON_UP;
			//					}
			//					UIEvent event = new UIEvent(evtType, Mouse.getEventButton(), new int[] { Mouse.getEventX(), Mouse.getEventY() });
			//					Model.getModel().getInteractionStack().addEvent(event);
			//					for (Action action : mouseActions) {
			//						runAction(action);
			//					}
			//				}
			//
			//			}
			//
			//			if (Keyboard.next()) {
			//				EventType evtType = null;
			//				if (Keyboard.getEventKeyState()) {
			//					evtType = EventType.KEYBOARD_DOWN;
			//				} else {
			//					evtType = EventType.KEYBOARD_UP;
			//				}
			//				UIEvent event = new UIEvent(evtType, Keyboard.getEventKey(), new int[] { Mouse.getEventX(), Mouse.getEventY() });
			//				Model.getModel().getInteractionStack().addEvent(event);
			//				LOGGER.debug("Sending keyboard event " + Keyboard.getEventKey());
			//				for (Action action : keyboardActions) {
			//					runAction(action);
			//				}
			//			}
			//
			//			int dx = Mouse.getDX();
			//			int dy = Mouse.getDY();
			//			if (dx != 0 || dy != 0) {
			//				for (Action action : mouseActions) {
			//					UIEvent event = new UIEvent(EventType.MOUSE_MOVE, Mouse.getEventButton(), new int[] { Mouse.getX(), Mouse.getY() });
			//					if (Model.getModel().getInteractionStack().getLastEvent().getEventType() != EventType.MOUSE_MOVE) {
			//						Model.getModel().getInteractionStack().addEvent(event);
			//					}
			//					runAction(action);
			//				}
			//			}

			// Handles the window close requested event
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}

			// Compute fps
			//			fpsCounter++;
			//			if (model.getCurrentTS() - lastFpsResetTs > 1000) {
			//				fps = fpsCounter;
			//
			//				// font.drawString(-font.getWidth(str) / 2, -width / 2, str, Color.white);
			//				lastFpsResetTs += 1000;
			//				fpsCounter = 0;
			//			}

		}
	}
}
