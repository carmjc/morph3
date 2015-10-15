package net.carmgate.morph.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jbox2d.common.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.carmgate.morph.conf.Conf;

/**
 * This class represents the properties of the view through which the world is seen.
 * The viewport is rectangular in shape.
 */
public class ViewPort {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewPort.class);

	@Inject private Conf conf;

	/** The intersection of the diagonals of the viewport (in world coordinates). */
	private final Vec2 focalPoint = new Vec2();

	/** The rotation of the scene around the focal point (in radians). */
	private final float rotation = 0;

	/** The zoom factor. > 1 means what you see is bigger. */
	private float zoomFactor;

	/** The intersection of the diagonals of the viewport (in <b>world coordinates</b>). */
	public Vec2 getFocalPoint() {
		return focalPoint;
	}

	// private Entity lockedOnEntity;

	/** The rotation of the scene around the focal point. */
	public float getRotation() {
		return rotation;
	}

	// public Entity getLockedOnEntity() {
	// return lockedOnEntity;
	// }

	/** The zoom factor. > 1 means what you see is bigger. */
	public float getZoomFactor() {
		return zoomFactor;
	}

	@PostConstruct
	private void init() {
		zoomFactor = conf.getFloatProperty("ui.viewPort.zoomFactor"); //$NON-NLS-1$
	}

	// public void setLockedOnEntity(Entity lockedOnEntity) {
	// this.lockedOnEntity = lockedOnEntity;
	// }

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

}
