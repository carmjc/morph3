package net.carmgate.morph.model.geometry;

public class Vector3f extends org.lwjgl.util.vector.Vector3f {

	public static Vector3f Z = new Vector3f(0, 0, 1f);

	private Vector3f(float x, float y, float z) {
		super(x, y, z);
	}
}
