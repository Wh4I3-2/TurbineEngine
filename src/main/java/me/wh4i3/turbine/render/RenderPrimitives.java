package me.wh4i3.turbine.render;

import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.Buffer.VertexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexBufferLayout;

public class RenderPrimitives {
	public static class RenderPrimitive {
		public final VertexArray vertexArray;
		public final VertexBuffer vertexBuffer;
		public final VertexBufferLayout vertexBufferLayout;
		public final IndexBuffer indexBuffer;

		public RenderPrimitive(float[] vertexData, int[] indexData, VertexBufferLayout vertexBufferLayout) {
			this.vertexArray = new VertexArray();
			this.vertexBuffer = new VertexBuffer(vertexData);
			this.vertexBufferLayout = vertexBufferLayout;

			this.vertexArray.addBuffer(this.vertexBuffer, this.vertexBufferLayout);

			this.indexBuffer = new IndexBuffer(indexData);
		}
	}

	public static RenderPrimitive TEXTURE_QUAD = new RenderPrimitive(
			new float[] {
					0.0f, 0.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 1.0f, 0.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					0.0f, 1.0f, 0.0f, 1.0f,
			},
			new int[] {
					0, 1, 2,
					2, 3, 0,
			},
			new VertexBufferLayout().pushFloat(2).pushFloat(2)
	);
}
