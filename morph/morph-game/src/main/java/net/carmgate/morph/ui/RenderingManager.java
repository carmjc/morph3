package net.carmgate.morph.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
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
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.animations.world.WorldAnimation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.inputs.InputHistory;
import net.carmgate.morph.ui.renderers.MorphFont;
import net.carmgate.morph.ui.renderers.RenderMode;
import net.carmgate.morph.ui.renderers.Renderable;
import net.carmgate.morph.ui.renderers.Renderer;
import net.carmgate.morph.ui.renderers.SelectRenderer;
import net.carmgate.morph.ui.renderers.entities.ship.ShipRenderer;
import net.carmgate.morph.ui.renderers.utils.RenderUtils;
import net.carmgate.morph.ui.renderers.utils.RenderUtils.TextAlign;
import net.carmgate.morph.ui.widgets.AbsoluteLayoutContainer;
import net.carmgate.morph.ui.widgets.MessagesPanel;
import net.carmgate.morph.ui.widgets.WidgetFactory;
import net.carmgate.morph.ui.widgets.shipeditor.ShipEditorPanel;

@Singleton
public class RenderingManager {

	public static MorphFont font;

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private UIContext uiContext;
	@Inject private WidgetFactory widgetFactory;
	@Inject private Messages messages;
	@Inject private InputHistory inputHistory;
	@Inject private RenderUtils renderUtils;
	@Inject private World world;
	@Inject private Instance<Renderer<? extends Renderable>> rendererInstances;

	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> renderers = new HashMap<>();
	private final Map<Class<? extends Renderable>, Renderer<? extends Renderable>> selectRenderers = new HashMap<>();

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

	/**
	 * Initialise the GL display
	 *
	 * @param width The width of the display
	 * @param height The height of the display
	 */
	public void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setTitle(conf.getProperty("ui.window.title")); //$NON-NLS-1$
			Display.setResizable(true);
		} catch (final LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initView();
	}

	public void initGui() {
		for (final Renderer<?> renderer : renderers.values()) {
			renderer.init();
		}

		uiContext.setWidgetRoot(widgetFactory.newInstance(AbsoluteLayoutContainer.class));

		MessagesPanel messagesWidget = widgetFactory.newInstance(MessagesPanel.class);
		messagesWidget.setPosition(new float[] { 0, 0, 0 });
		uiContext.getWidgetRoot().add(messagesWidget);

		ShipEditorPanel shipEditorPanel = widgetFactory.newInstance(ShipEditorPanel.class);
		shipEditorPanel.setPosition(new float[] { uiContext.getWindow().getWidth() / 2, 0, 0 });
		uiContext.getWidgetRoot().add(shipEditorPanel);
	}

	/**
	 * Inits the view, viewport, window, etc. This should be called at init and when the view changes (window is resized for instance).
	 */
	public void initView() {

		final int width = Display.getWidth();
		final int height = Display.getHeight();
		LOGGER.debug("init view: " + width + "x" + height); //$NON-NLS-1$ //$NON-NLS-2$

		// set clear color - Wont be needed once we have a background
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 0f);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(-width / 2, width / 2, height / 2, -height / 2, 1, -1);
		GL11.glViewport(0, 0, width, height);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if (font == null) {
			Font awtFont;
			try {
				awtFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(conf.getProperty("ui.font"))); //$NON-NLS-1$
				awtFont = awtFont.deriveFont(conf.getFloatProperty("ui.font.size")); // set font size //$NON-NLS-1$
				// font = new TrueTypeFont(awtFont, true);

				Image image = createMipmapImage(conf.getProperty("ui.font.angel.tga"));
				font = new MorphFont(conf.getProperty("ui.font.angel"), image);
				font.setFontSize(14);

			} catch (FontFormatException | IOException | SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@PostConstruct
	private void postContruct() {
		rendererInstances.forEach((renderer) -> {
			registerRenderer(renderer);
		});
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
				if (paramType.getRawType().equals(SelectRenderer.class)) {
					final Class<? extends Renderable> type = (Class<? extends Renderable>) paramType.getActualTypeArguments()[0];
					selectRenderers.put(type, renderer);
					LOGGER.debug("Added new selectRenderer: " + renderer.getClass().getName() + " for " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	public void renderComponentsAnimation() {
		for (final Ship ship : world.getShips()) {
			final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
			final float zoomFactor = uiContext.getViewport().getZoomFactor();
			GL11.glScalef(zoomFactor, zoomFactor, 1);
			GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);

			Collection<Component> components = ship.getComponents().values();
			components.forEach(cmp -> {
				if (cmp.isActive()) {
					Animation anim = cmp.getAnimation();
					if (anim != null) {
						Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(anim.getClass());
						if (anim.getEnd() > world.getTime()) {
							renderer.render(anim, 1f);
						}
						if (anim.getEnd() + anim.getCoolDown() < world.getTime()) {
							anim.setEnd(anim.getEnd() + anim.getCoolDown() + anim.getDuration());
						}
					}
				}
			});

			GL11.glTranslatef(focalPoint.x, focalPoint.y, 0);
			GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
		}
	}

	public void renderGui() {
		renderGuiForSelectedShip();

		float x = uiContext.getWindow().getWidth() / 2 - 2;
		float y = uiContext.getWindow().getHeight() / 2 - 2;

		// Render state zone
		int line = 0;
		if (world.isTimeFrozen()) {
			renderUtils.renderText(font, x, y, messages.getString("ui.game.paused"), line--, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
		}

		// Render debug information
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			String[] strArray = inputHistory.toString().split("\n");
			line = -strArray.length + 1;
			for (String str : strArray) {
				renderUtils.renderText(font, -x, y, str, line++, Color.white, TextAlign.LEFT);
			}
		}

		float borderRightX = -uiContext.getWindow().getWidth() / 2;
		float borderTopY = -uiContext.getWindow().getHeight() / 2;

		GL11.glTranslatef(borderRightX, borderTopY, 0);
		uiContext.getWidgetRoot().renderWidget();
		GL11.glTranslatef(-borderRightX, -borderTopY, 0);
	}

	private void renderGuiForSelectedShip() {
		Ship ship = uiContext.getSelectedShip();
		float borderLeftX = uiContext.getWindow().getWidth() / 2 - 4;
		float borderTopY = -uiContext.getWindow().getHeight() / 2 + 2;
		int line = 1;
		if (ship != null) {
			if (uiContext.getRenderMode() == RenderMode.DEBUG) {
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.distance"), ship.debug1.length()), line++, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.speed"), ship.getSpeed().length()), line++, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.accel"), ship.getAccel().length()), line++, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.health"), ship.getIntegrity() * 100), line++, Color.white, TextAlign.RIGHT); //$NON-NLS-1$
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.eco"), ship.getEnergy(), ship.getResources()), line++, Color.white, //$NON-NLS-1$
						TextAlign.RIGHT);
				renderUtils.renderText(font, borderLeftX, borderTopY,
						MessageFormat.format(messages.getString("ui.selectedShip.ecoMax"), ship.getEnergyMax(), ship.getResourcesMax()), line++, Color.white, //$NON-NLS-1$
						TextAlign.RIGHT);

				for (Component c : ship.getComponents().values()) {
					Color color = Color.white;
					if (!c.isActive()) {
						color = Color.gray;
					}

					GL11.glTranslatef(borderLeftX - 5, borderTopY + font.getTargetFontSize() * line - 10, 0);
					ComponentType cmpType = c.getClass().getAnnotation(ComponentKind.class).value();
					float[] cmpColor = cmpType.getColor();
					renderUtils.renderQuad(0, 0, 5, 5, cmpColor);
					GL11.glTranslatef(-(borderLeftX - 5), -(borderTopY + font.getTargetFontSize() * line - 10), 0);

					renderUtils.renderText(font, borderLeftX - 10, borderTopY,
							MessageFormat.format(messages.getString("ui.selectedShip.components"), c.getClass().getSimpleName(), c.getEnergyDt(), //$NON-NLS-1$
									c.getResourcesDt()),
							line++, color, TextAlign.RIGHT);

				}
			}
		}

	}

	public void renderPhysical() {
		final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
		final float zoomFactor = uiContext.getViewport().getZoomFactor();
		GL11.glScalef(zoomFactor, zoomFactor, 1);
		GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);

		for (PhysicalEntity entity : world.getPhysicalEntities()) {
			if (!(entity instanceof Ship)) {
				final Vector2f pos = entity.getPos();
				GL11.glTranslatef(pos.x, pos.y, 0);
				Renderer<PhysicalEntity> renderer = (Renderer<PhysicalEntity>) renderers.get(entity.getClass());
				renderer.render(entity, 1f);
				GL11.glTranslatef(-pos.x, -pos.y, 0);
			}
		}

		final ShipRenderer shipRenderer = (ShipRenderer) renderers.get(Ship.class);
		if (shipRenderer != null) {
			for (final Ship ship : world.getShips()) {
				final Vector2f pos = ship.getPos();
				GL11.glTranslatef(pos.x, pos.y, 0);
				float alpha = Math.min(((float) world.getTime() - ship.getCreationTime()) / 500, 1f);
				shipRenderer.render(ship, alpha);
				GL11.glTranslatef(-pos.x, -pos.y, 0);
			}
		}

		GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
		GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
	}

	public void renderWorldAnimation() {
		final Vector2f focalPoint = uiContext.getViewport().getFocalPoint();
		final float zoomFactor = uiContext.getViewport().getZoomFactor();
		GL11.glScalef(zoomFactor, zoomFactor, 1);
		GL11.glTranslatef(-focalPoint.x, -focalPoint.y, 0);

		List<WorldAnimation> toBeRemoved = new ArrayList<>();

		for (WorldAnimation wAnim : world.getWorldAnimations()) {
			Renderer<Animation> renderer = (Renderer<Animation>) renderers.get(wAnim.getClass());
			renderer.render(wAnim, 1f);

			if (wAnim.getDuration() < world.getTime() - wAnim.getCreationTime()) {
				toBeRemoved.add(wAnim);
			}
		}

		// Clean animations list
		for (WorldAnimation anim : toBeRemoved) {
			world.getWorldAnimations().remove(anim);
		}

		GL11.glTranslatef(+focalPoint.x, +focalPoint.y, 0);
		GL11.glScalef(1 / zoomFactor, 1 / zoomFactor, 1);
	}

}
