package net.carmgate.morph.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;

import net.carmgate.morph.Messages;
import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipAdded;
import net.carmgate.morph.events.world.entities.ship.ShipUpdated;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.Asteroid;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.Planet;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.GeoUtils;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.particles.Particle;
import net.carmgate.morph.ui.particles.ParticleEngine;
import net.carmgate.morph.ui.renderers.MorphDebugDraw;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderable;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.StringRenderer;
import net.carmgate.morph.ui.renderers.entities.ship.ShipRenderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.widgets.Widget;
import net.carmgate.morph.ui.widgets.WidgetFactory;
import net.carmgate.morph.ui.widgets.components.ComponentWidget;
import net.carmgate.morph.ui.widgets.containers.ColumnLayoutWidgetContainer;
import net.carmgate.morph.ui.widgets.containers.RootContainer;

@Singleton
public class RenderingManager {

	public static AngelCodeFont font;

	private Map<ComponentType, Texture> cmpTextures = new HashMap<>();

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private UIContext uiContext;
	@Inject private WidgetFactory widgetFactory;
	@Inject private Messages messages;
	@Inject private InputHistory inputHistory;
	@Inject private RenderUtils renderUtils;
	@Inject private MWorld world;
	@Inject private ParticleEngine particleEngine;
	@Inject private Instance<Renderer<? extends Renderable>> rendererInstances;
	@Inject private MEventManager eventManager;
	@Inject private PhysicalEntityFactory physicalEntityFactory;
	@Inject private StringRenderer stringRenderer;
	@Inject private MorphDebugDraw debugDraw;

	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();
	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> selectRenderers = new HashMap<>();

	private Texture particleTexture;
	private final FloatBuffer worldVpFb = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer guiVpFb = BufferUtils.createFloatBuffer(16);

	private Image createMipmapImage(String ref) throws SlickException {
		// this implementation is subject to change...
		try {
			InputStream in = ResourceLoader.getResourceAsStream(ref);
			LoadableImageData imageData = ImageDataFactory.getImageDataFor(ref);
			ByteBuffer buf = imageData.loadImage(new BufferedInputStream(in),
					false, null);
			ImageData.Format fmt = imageData.getFormat();
			int minFilter = GL11.GL_LINEAR_MIPMAP_NEAREST;
			int magFilter = GL11.GL_LINEAR;

			// Texture tex = renderUtils.getTexture(ref, in);
			Texture tex = InternalTextureLoader.get().createTexture(
					imageData, // the image data holding width/height/format
					buf, // the buffer of data
					ref, // the ref for the TextureImpl
					GL11.GL_TEXTURE_2D, // what you will usually use
					minFilter, magFilter, // min and mag filters
					true, // generate mipmaps automatically
					fmt); // the internal format for the texture or null for RGBA default
			return new Image(tex);
		} catch (IOException e) {
			Log.error("error loading image", e);
			throw new SlickException("error loading image " + e.getMessage());
		}
	}

	public FloatBuffer getGuiVpFb() {
		return guiVpFb;
	}

	public FloatBuffer getWorldVpFb() {
		return worldVpFb;
	}

	/**
	 * Initialise the GL display
	 *
	 * @param width The width of the display
	 * @param height The height of the display
	 */
	public void initGL(int width, int height) {
		try {

			// PixelFormat pixelFormat = new PixelFormat();
			// ContextAttribs contextAtrributes = new ContextAttribs(3, 3)
			// .withForwardCompatible(true)
			// .withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setResizable(false);
			Display.setTitle(conf.getProperty("ui.window.title")); //$NON-NLS-1$
			Display.create();
		} catch (final LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initView();
	}

	public void initGui() {
		// initialize renderers
		for (final Renderer<?> renderer : renderers.values()) {
			renderer.init();
		}

		// initialize fonts
		font = new AngelCodeFont(conf.getProperty("ui.font.angel"), conf.getProperty("ui.font.angel.tga"));

		// initialize gui
		uiContext.setWidgetRoot(widgetFactory.newInstance(RootContainer.class));
	}

	/**
	 * Inits the view, viewport, window, etc. This should be called at init and when the view changes (window is resized for instance).
	 */
	public void initView() {

		// set clear color - Wont be needed once we have a background
		GL11.glClearColor(147f / 256, 114f / 256, 132f / 256, 1f);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		// if (font == null) {
		// Font awtFont;
		// try {
		// awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(conf.getProperty("ui.font"))); //$NON-NLS-1$
		// awtFont = awtFont.deriveFont(conf.getFloatProperty("ui.font.size")); // set font size //$NON-NLS-1$
		// // font = new TrueTypeFont(awtFont, true);
		//
		// Image image = createMipmapImage(conf.getProperty("ui.font.angel.tga"));
		// font = new MorphFont(conf.getProperty("ui.font.angel"), image);
		// font.setFontSize(14);
		//
		// } catch (FontFormatException | IOException | SlickException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	@SuppressWarnings("unused")
	private void onShipAdded(@MObserves ShipAdded shipAdded) {
		if (shipAdded.getShip() == world.getPlayerShip()) {
			refreshCmpBar();
		}
	}

	@SuppressWarnings("unused")
	private void onShipAdded(@MObserves ShipUpdated shipUpdated) {
		if (shipUpdated.getShip() == world.getPlayerShip()) {
			refreshCmpBar();
		}
	}

	@PostConstruct
	private void postContruct() {
		rendererInstances.forEach((renderer) -> {
			registerRenderer(renderer);
		});
		eventManager.scanAndRegister(this);
	}

	private void refreshCmpBar() {
		// remove old widgets
		ColumnLayoutWidgetContainer cmpBarWidget = uiContext.getWidgetRoot().getCmpBarWidget();
		for (Widget widget : cmpBarWidget.getWidgets()) {
			// FIXME
		}
		cmpBarWidget.getWidgets().clear();

		// add new widgets
		for (Component cmp : world.getPlayerShip().getComponents().values()) {
			ComponentWidget cmpWidget = widgetFactory.newInstance(ComponentWidget.class);
			cmpWidget.setCmp(cmp);
			cmpBarWidget.add(cmpWidget);

			if (cmpWidget.getShape() != null && cmpWidget.getPosition() != null) {
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.STATIC;
				Body body = world.getBox2dWorld().createBody(bodyDef);

				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.filter.groupIndex = -2;
				fixtureDef.shape = cmpWidget.getShape();

				body.createFixture(fixtureDef);
				body.setUserData(cmpWidget);
				Vec2 wPosition = new Vec2(cmpWidget.getPosition()[0] / 1000 + cmpWidget.getWidth() / 2000,
						cmpWidget.getPosition()[1] / 1000 + cmpWidget.getHeight() / 2000);
				LOGGER.debug("" + cmp + " position: " + wPosition);
				body.setTransform(wPosition, 0);

				cmpWidget.setBody(body);
			}
		}
	}

	public void registerRenderer(final Renderer<? extends Renderable> renderer) {
		final Type[] interfaces = renderer.getClass().getGenericInterfaces();
		for (final Type interf : interfaces) {
			if (interf instanceof ParameterizedType) {
				final ParameterizedType paramType = (ParameterizedType) interf;
				if (paramType.getRawType().equals(Renderer.class)) {
					final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
					renderers.put(type, renderer);
					LOGGER.debug("Added new renderer: " + renderer.getClass().getName() + " for " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				// if (paramType.getRawType().equals(SelectRenderer.class)) {
				// final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
				// selectRenderers.put(type, renderer);
				// LOGGER.debug("Added new selectRenderer: " + renderer.getClass().getName() + " for " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				// }
			}
		}
	}

	public void renderAnimations() {
		for (Entry<Class<? extends Animation>, List<Animation>> anims : world.getAnimations().entrySet()) {
			Renderer<Renderable> renderer = (Renderer<Renderable>) renderers.get(anims.getKey());
			renderer.prepare();
			for (Animation anim : anims.getValue()) {
				if (anim.getEnd() > world.getTime()) {
					renderer.render(anim, 1, worldVpFb);
				}
				if (anim.getEnd() + anim.getCoolDown() < world.getTime()) {
					anim.setEnd(anim.getEnd() + anim.getCoolDown() + anim.getDuration());
				}
			}
			renderer.clean();
		}
	}

	public void renderBackground() {
		// render planet background
		Planet planet = physicalEntityFactory.newInstance(Planet.class);
		Renderer<Renderable> planetRenderer = (Renderer<Renderable>) renderers.get(planet.getClass());
		planetRenderer.prepare();
		planetRenderer.render(planet, 1, worldVpFb);
		planetRenderer.clean();
	}

	public void renderBgParticles() {
		renderParticles(particleEngine.getBgParticles());
	}

	// public void renderComponentsAnimation() {
	// for (final Ship ship : world.getShips()) {
	// final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
	// final float zoomFactor = uiContext.getViewport().getZoomFactor();
	// GL11.glScalef(zoomFactor, zoomFactor, 1);
	// GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);
	//
	// Collection<Component> components = ship.getComponents().values();
	// components.forEach(cmp -> {
	// if (cmp.isActive()) {
	// Animation anim = cmp.getAnimation();
	// if (anim != null) {
	// Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(anim.getClass());
	// if (anim.getEnd() > world.getTime()) {
	// renderer.render(anim, 0.5f);
	// }
	// if (anim.getEnd() + anim.getCoolDown() < world.getTime()) {
	// anim.setEnd(anim.getEnd() + anim.getCoolDown() + anim.getDuration());
	// }
	// }
	// }
	// });
	//
	// GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
	// GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
	// }
	// }

	// public void renderFgParticles() {
	// renderParticles(particleEngine.getFgParticles());
	// }

	public void renderGui() {
		// renderGuiForSelectedShip();
		//
		// float x = uiContext.getWindow().getWidth() / 2 - 2;
		// float y = uiContext.getWindow().getHeight() / 2 - 2;
		//
		// // Render state zone
		// int line = 0;
		// if (world.isTimeFrozen()) {
		// renderUtils.renderText(font, x, y, messages.getString("ui.game.paused"), line--, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
		// }
		//
		// // Render debug information
		// if (uiContext.getRenderMode() == RenderMode.DEBUG) {
		// String[] strArray = inputHistory.toString().split("\n");
		// line = -strArray.length + 1;
		// for (String str : strArray) {
		// renderUtils.renderText(font, -x, y, str, line++, Color.white, TextAlign.LEFT);
		// }
		// }
		//
		// float borderRightX = -uiContext.getWindow().getWidth() / 2;
		// float borderTopY = -uiContext.getWindow().getHeight() / 2;
		//
		// GL11.glTranslatef(borderRightX, borderTopY, 0);

		Matrix4f m = new Matrix4f();
		m.setIdentity();

		uiContext.getWidgetRoot().renderWidget(m, guiVpFb);
		// GL11.glTranslatef(-borderRightX, -borderTopY, 0); }

		// stringRenderer.prepare();
		// StringRenderable str = new StringRenderable();
		// str.setStr("This is a great thing to be able to render this\ntest\nWhoo !!! |");
		// str.setSize(20);
		// str.getPos().copy(-uiContext.getWindow().getWidth() / 2 + 10,
		// uiContext.getWindow().getHeight() / 2 - 10);
		// stringRenderer.render(str, 1, guiVpFb);
		// stringRenderer.clean();
	}

	private void renderGuiForSelectedShip() {
		Ship ship = uiContext.getSelectedShip();
		float borderLeftX = uiContext.getWindow().getWidth() / 2 - 4;
		float borderTopY = -uiContext.getWindow().getHeight() / 2 + 2;
		int line = 1;
		float fontSize = 20;
		// if (ship != null) {
		// if (uiContext.getRenderMode() == RenderMode.DEBUG) {
		// StringRenderable stringRenderable = renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.distance"), ship.debug1.length()), line++, Color.white, TextAlign.RIGHT, //$NON-NLS-1$
		// fontSize);
		// ???find a way to put Stringrenderables somewhere and to have them renewed/refreshed more cleverly???
		// renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.speed"), ship.getSpeed().length()), line++, Color.white, TextAlign.RIGHT, //$NON-NLS-1$
		// fontSize);
		// renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.accel"), ship.getAccel().length()), line++, Color.white, TextAlign.RIGHT, //$NON-NLS-1$
		// fontSize);
		// renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.health"), ship.getIntegrity() * 100), line++, Color.white, TextAlign.RIGHT, //$NON-NLS-1$
		// fontSize);
		// renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.eco"), ship.getEnergy(), ship.getResources()), line++, Color.white, //$NON-NLS-1$
		// TextAlign.RIGHT, fontSize);
		// renderUtils.getStringRenderable(font, borderLeftX, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.ecoMax"), ship.getEnergyMax(), ship.getResourcesMax()), line++, Color.white, //$NON-NLS-1$
		// TextAlign.RIGHT, fontSize);
		//
		// for (Component c : ship.getComponents().values()) {
		// Color color = Color.white;
		// if (!c.isActive()) {
		// color = Color.gray;
		// }
		//
		// ComponentType cmpType = c.getClass().getAnnotation(ComponentKind.class).value();
		// float[] cmpColor = cmpType.getColor();
		// renderUtils.renderQuad(0, 0, 5, 5, cmpColor);
		//
		// renderUtils.getStringRenderable(font, borderLeftX - 10, borderTopY,
		// MessageFormat.format(messages.getString("ui.selectedShip.components"), c.getClass().getSimpleName(), c.getEnergyDt(), //$NON-NLS-1$
		// c.getResourcesDt()),
		// line++, color, TextAlign.RIGHT, fontSize);
		//
		// }
		// }
		// }

	}

	private void renderParticles(List<Particle> particles) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		final Vec2 focalPoint = uiContext.getViewport().getFocalPoint();
		final float zoomFactor = uiContext.getViewport().getZoomFactor();
		GL11.glScalef(zoomFactor, zoomFactor, 1);
		GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);
		int nb = 0;

		for (Particle p : particles) {
			Renderer<Particle> pRenderer = (Renderer<Particle>) renderers.get(p.getClass());
			GL11.glTranslatef(p.getPos().x, p.getPos().y, 0);
			GL11.glRotatef(p.getRotation(), 0, 0, 1);
			pRenderer.render(p, 1f, worldVpFb);
			GL11.glRotatef(-p.getRotation(), 0, 0, 1);
			GL11.glTranslatef(-p.getPos().x, -p.getPos().y, 0);
			nb++;
		}

		GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
		GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);// GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void renderPhysical() {
		Ship playerShip = world.getPlayerShip();
		float appearanceDistanceSq = playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius() * 81 / 64;
		float interactionDistanceSq = playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius();

		Renderer<PhysicalEntity> renderer = (Renderer<PhysicalEntity>) renderers.get(Asteroid.class);
		renderer.prepare();
		for (PhysicalEntity entity : world.getPhysicalEntities()) {
			float distanceToEntitySq = GeoUtils.distanceToSquared(entity.getPosition(), playerShip.getPosition());
			if (uiContext.getRenderMode() == RenderMode.DEBUG || distanceToEntitySq < appearanceDistanceSq) {
				float ratio = 1;
				if (uiContext.getRenderMode() != RenderMode.DEBUG && distanceToEntitySq > interactionDistanceSq) {
					ratio = (appearanceDistanceSq - distanceToEntitySq) / (appearanceDistanceSq - interactionDistanceSq);
				}

				if (!(entity instanceof Ship)) {
					renderer.render(entity, ratio, worldVpFb);
				}
			}
		}
		renderer.clean();

		final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			for (final Ship ship : world.getShips()) {
				debugDraw.drawCircle(ship.getBody().getPosition(), 0.2f, Color3f.WHITE);
			}
		}

		shipRenderer.prepare();
		for (final Ship ship : world.getShips()) {
			float distanceToPlayerShipSq = GeoUtils.distanceToSquared(ship.getPosition(), playerShip.getPosition());
			if (uiContext.getRenderMode() == RenderMode.DEBUG || distanceToPlayerShipSq < appearanceDistanceSq) {
				float visibilityRatio = 1;
				if (uiContext.getRenderMode() != RenderMode.DEBUG && distanceToPlayerShipSq > interactionDistanceSq) {
					visibilityRatio = (appearanceDistanceSq - distanceToPlayerShipSq) / (appearanceDistanceSq - interactionDistanceSq);
				}

				float appearanceAlpha = Math.min(((float) world.getTime() - ship.getCreationTime()) / 500, 1f);
				shipRenderer.render(ship, appearanceAlpha * visibilityRatio, worldVpFb);
			}
		}
		shipRenderer.clean();
	}

	// public void renderWorldAnimation() {
	// final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
	// final float zoomFactor = uiContext.getViewport().getZoomFactor();
	// GL11.glScalef(zoomFactor, zoomFactor, 1);
	// GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);
	//
	// List<WorldAnimation> toBeRemoved = new ArrayList<>();
	//
	// for (WorldAnimation wAnim : world.getWorldAnimations()) {
	// Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(wAnim.getClass());
	// renderer.render(wAnim, 1f);
	//
	// if (wAnim.getDuration() < world.getTime() - wAnim.getCreationTime()) {
	// toBeRemoved.add(wAnim);
	// }
	// }
	//
	// // Clean animations list
	// for (WorldAnimation anim : toBeRemoved) {
	// world.getWorldAnimations().remove(anim);
	// }
	//
	// GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
	// GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
	// }

}
