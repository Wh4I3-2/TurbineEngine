package me.wh4i3.turbine.render.Buffer;

import static org.lwjgl.opengl.GL45C.*;

public class VertexBuffer {
	private final int rendererID;

	public VertexBuffer(float[] data) {
		rendererID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rendererID);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
	}

	public VertexBuffer(float[] data, VertexBufferLayout layout) {
		rendererID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rendererID);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
	}

	public VertexBuffer delete() {
		glDeleteBuffers(rendererID);
		return null;
	}

	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, rendererID);
	}
	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
