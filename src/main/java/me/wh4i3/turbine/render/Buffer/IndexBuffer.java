package me.wh4i3.turbine.render.Buffer;

import static org.lwjgl.opengl.GL45C.*;

public class IndexBuffer {
	private final int rendererID;
	private final int count;

	public IndexBuffer(int[] data) {
		rendererID = glGenBuffers();
		this.count = data.length;
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, rendererID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
	}

	public VertexBuffer delete() {
		glDeleteBuffers(rendererID);
		return null;
	}

	public void bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, rendererID);
	}

	public void unbind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public int count() {
		return this.count;
	}

	public int type() {
		return GL_UNSIGNED_INT;
	}
}
