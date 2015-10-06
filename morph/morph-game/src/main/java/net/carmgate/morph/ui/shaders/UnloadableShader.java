package net.carmgate.morph.ui.shaders;

public class UnloadableShader extends RuntimeException {

	public UnloadableShader(String msg) {
		super(msg);
	}

	public UnloadableShader(String msg, Throwable cause) {
		super(msg, cause);
	}

}
