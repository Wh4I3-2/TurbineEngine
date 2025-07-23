package me.wh4i3.turbine.render.Buffer;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL45C.*;

public class VertexBufferLayout {
	public record VertexBufferElement(int type, int count, boolean normalized) {
		public int size() {
			return sizeOfType(type);
		}

		public static int sizeOfType(int type) {
			switch (type) {
				case GL_FLOAT: return Float.BYTES;
				case GL_UNSIGNED_INT: return Integer.BYTES;
				case GL_UNSIGNED_BYTE: return Byte.BYTES;
			}
			return 0;
		}
	}

	private final int rendererID;
	private final List<VertexBufferElement> elements;

	private int stride;

	public VertexBufferLayout(int rendererID) {
		this.rendererID = rendererID;
		this.elements = new ArrayList<>();
		this.stride = 0;
	}

	public VertexBufferLayout() {
		this(0);
	}

	public void pushFloat(int count) {
		elements.addLast(new VertexBufferElement(GL_FLOAT, count, false));
		this.stride += count * VertexBufferElement.sizeOfType(GL_FLOAT);
	}

	public void pushInt(int count) {
		elements.addLast(new VertexBufferElement(GL_UNSIGNED_INT, count, false));
		this.stride += count * VertexBufferElement.sizeOfType(GL_UNSIGNED_INT);
	}

	public void pushByte(int count) {
		elements.addLast(new VertexBufferElement(GL_UNSIGNED_BYTE, count, true));
		this.stride += count * VertexBufferElement.sizeOfType(GL_UNSIGNED_BYTE);
	}

	public int stride() {
		return this.stride;
	}

	public int rendererID() {
		return this.rendererID;
	}

	public List<VertexBufferElement> elements() {
		return this.elements;
	}
}
