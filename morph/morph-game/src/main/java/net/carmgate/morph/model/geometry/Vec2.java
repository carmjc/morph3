package net.carmgate.morph.model.geometry;

@SuppressWarnings("serial")
public class Vec2 extends org.lwjgl.util.vector.Vector2f {

	public static final Vec2 NULL = new Vec2();
	public static final Vec2 J = new Vec2(0, -1);

	public Vec2() {
		super(0, 0);
	}

	public Vec2(float x, float y) {
		super(x, y);
	}

	public Vec2(Vec2 v) {
		if (v != null) {
			copy(v);
		}
	}

	public Vec2 add(Vec2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	@Override
	public Vec2 clone() {
		return new Vec2(this);
	}

	public Vec2 copy(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vec2 copy(Vec2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public float distanceTo(Vec2 to) {
		return (float) Math.hypot(to.x - x, to.y - y);
	}

	public boolean isNull() {
		return x == 0 && y ==0;
	}

	public Vec2 rotate(float angle) {
		angle = (float) (angle / 180 * Math.PI);
		float cosAngle = (float) Math.cos(angle);
		float sinAngle = (float) Math.sin(angle);
		float t = x * cosAngle - y * sinAngle;
		y = x * sinAngle + y * cosAngle;
		x = t;
		return this;
	}

	public Vec2 rotateOrtho() {
		float t = x;
		x = -y;
		y = t;
		return this;
	}

	@Override
	public Vec2 scale(float factor) {
		x *= factor;
		y *= factor;
		return this;
	}

	public Vec2 sub(Vec2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
