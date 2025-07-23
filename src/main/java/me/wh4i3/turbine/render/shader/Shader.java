package me.wh4i3.turbine.render.shader;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.resource.ResourceKey;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL45C.*;
import static org.lwjgl.system.MemoryUtil.memASCII;

public class Shader {
	public static final String basePath = "assets/shaders/";

	private final int programID;

	private final ResourceKey vertexKey;
	private final ResourceKey fragmentKey;

	private final Map<String, Integer> uniforms;
	private Set<String> foundUniforms;

	public Shader(ResourceKey vertexKey, ResourceKey fragmentKey) {
		this.vertexKey = vertexKey;
		this.fragmentKey = fragmentKey;

		this.foundUniforms = new HashSet<>();

		this.programID = glCreateProgram();
		int vsh = compileShader(GL_VERTEX_SHADER, parseShader(this.vertexKey));
		int fsh = compileShader(GL_FRAGMENT_SHADER, parseShader(this.fragmentKey));

		glAttachShader(this.programID, vsh);
		glAttachShader(this.programID, fsh);
		glLinkProgram(this.programID);
		glValidateProgram(this.programID);

		glDeleteShader(vsh);
		glDeleteShader(fsh);

		glDetachShader(this.programID, vsh);
		glDetachShader(this.programID, fsh);

		final Map<String, Integer> foundUniformLocations = new HashMap<>();

		this.foundUniforms.forEach(uniform -> {
			foundUniformLocations.put(uniform, glGetUniformLocation(this.programID, uniform));
		});
		this.uniforms = Collections.unmodifiableMap(foundUniformLocations);
		this.foundUniforms = null;
	}

	private String parseShader(ResourceKey key) {
		InputStream file = key.withPrefix(basePath).openStream();

		if (file == null) {
			throw new RuntimeException("ERROR SHADER FILE DOESN'T EXIST!");
		}

		Scanner scanner = new Scanner(file);

		StringBuilder output = new StringBuilder();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("uniform")) {
				String[] splitLine = line.split(" ");
				if (splitLine.length >= 3) {
					String uniform = splitLine[splitLine.length-1];
					this.foundUniforms.add(uniform.substring(0, uniform.length()-1));
				}
			}
			output.append(line).append("\n");
		}

		return output.toString();
	}

	private int compileShader(int type, String source) {
		int id = glCreateShader(type);

		glShaderSource(id, source);
		glCompileShader(id);

		int[] result = {0};
		glGetShaderiv(id, GL_COMPILE_STATUS, result);

		if (result[0] == GL_FALSE) {
			int[] length = {0};
			glGetShaderiv(id, GL_INFO_LOG_LENGTH, length);
			ByteBuffer message = ByteBuffer.allocate(length[0] * Character.SIZE);
			glGetShaderInfoLog(id, length, message);
			Main.LOGGER.error("FAILED TO COMPILE {} SHADER!", type == GL_VERTEX_SHADER ? "vertex" : "fragment");
			Main.LOGGER.error(memASCII(message));

			glDeleteShader(id);
			return 0;
		}

		return id;
	}

	public void bind() {
		glUseProgram(this.programID);
	}

	public Map<String, Integer> uniforms() {
		return this.uniforms;
	}

	public void unbind() {
		glUseProgram(0);
	}

	public int programID() {
		return this.programID;
	}
}
