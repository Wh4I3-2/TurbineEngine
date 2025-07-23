package me.wh4i3.turbine.render.Buffer;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.render.Window;

import java.util.List;

import static org.lwjgl.opengl.GL45C.*;

public class VertexArray {
	private final int rendererID;

	public VertexArray() {
		this.rendererID = glGenVertexArrays();
	}

	public VertexArray delete() {
		glDeleteVertexArrays(this.rendererID);
		return null;
	}

	public void bind() {
		glBindVertexArray(this.rendererID);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void addBuffer(VertexBuffer vertexBuffer, VertexBufferLayout layout) {
		bind();
		vertexBuffer.bind();

		List<VertexBufferLayout.VertexBufferElement> elements = layout.elements();
		int offset = 0;

		for (int i = 0; i < elements.size(); i++) {
			VertexBufferLayout.VertexBufferElement element = elements.get(i);
			glEnableVertexAttribArray(i);
			glVertexAttribPointer(i, element.count(), element.type(), element.normalized(), layout.stride(), offset);
			offset += element.count() * element.size();
		}
	}

	public int rendererID() {
		return this.rendererID;
	}
}
