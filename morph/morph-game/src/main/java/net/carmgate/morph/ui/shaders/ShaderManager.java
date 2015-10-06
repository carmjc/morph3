package net.carmgate.morph.ui.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

@Singleton
public class ShaderManager {

	private Map<String, Integer> programs = new HashMap<>();

	public int getProgram(String prog) {
		Integer progId = programs.get(prog);
		if (progId == null) {
			progId = loadProgram(prog);
			programs.put(prog, progId);
		}
		return progId;
	}

	// NEW
	private int loadProgram(String programName) {
		int errorCheckValue = GL11.glGetError();

		// Load the vertex shader
		int vsId = loadShader("shaders/" + programName + ".vert.glsl", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		int fsId = loadShader("shaders/" + programName + ".frag.glsl", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		int pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);

		GL20.glLinkProgram(pId);
		GL20.glValidateProgram(pId);

		errorCheckValue = GL11.glGetError();
		if (errorCheckValue != GL11.GL_NO_ERROR) {
			System.out.println("ERROR - Could not create the shaders:" + GLU.gluErrorString(errorCheckValue));
			System.exit(-1);
		}

		return pId;
	}

	// NEW
	private int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		return shaderID;
	}

}
