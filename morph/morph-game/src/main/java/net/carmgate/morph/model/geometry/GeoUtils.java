package net.carmgate.morph.model.geometry;

import org.lwjgl.util.vector.Vector2f;

public class GeoUtils {

	public static float angleWith(org.jbox2d.common.Vec2 u, org.jbox2d.common.Vec2 v) {
		float angle = Vector2f.angle(new Vector2f(u.x, u.y), new Vector2f(v.x, v.y));
		float crossMag = u.x * v.y - v.x * u.y;
		if (crossMag > 0) {
			angle = -angle;
		}
		return angle;
	}

	public static float distanceTo(org.jbox2d.common.Vec2 from, org.jbox2d.common.Vec2 to) {
		return (float) Math.sqrt((to.x - from.x) * (to.x - from.x) + (to.y - from.y) * (to.y - from.y));
	}

	public static float distanceToSquared(org.jbox2d.common.Vec2 from, org.jbox2d.common.Vec2 to) {
		return (to.x - from.x) * (to.x - from.x) + (to.y - from.y) * (to.y - from.y);
	}

}
