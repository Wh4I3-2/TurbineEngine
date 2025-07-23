package me.wh4i3.turbine.render;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.gamedata.gameobject.GameObject;
import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.shader.Shader;
import me.wh4i3.turbine.render.shader.material.AbstractMaterial;
import org.joml.Matrix4f;
import org.joml.Vector2f;


import static org.lwjgl.opengl.GL45C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
	private static Renderer instance;

	public static Renderer instance() {
		if (instance == null) {
			instance = new Renderer();
		}
		return instance;
	};


	public Matrix4f projectionMatrix = new Matrix4f().ortho(
			0.0f, Window.VIEWPORT_WIDTH,
			0.0f, Window.VIEWPORT_HEIGHT,
			0.001f, 1000.0f
	);

	//public Matrix4f projectionMatrix = new Matrix4f().perspective(90.0f, 16.0f/9.0f, 0.0001f, 1000.0f);

	public Matrix4f viewMatrix = new Matrix4f().identity();
	public Vector2f subPixelView = new Vector2f(0);

	public Matrix4f modelMatrix = new Matrix4f().identity();


	public Matrix4f mvp() {
		return new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	public void updateCommonUniforms(AbstractMaterial material) {
		Shader shader = material.shader();

		if (shader.uniforms().containsKey("u_Time")) {
			glUniform1f(shader.uniforms().get("u_Time"), ((float) Main.tick / Main.TPS) + Main.deltaTime());
		}

		if (shader.uniforms().containsKey("u_MVP")) {
			glUniformMatrix4fv(shader.uniforms().get("u_MVP"), false, mvp().get(new float[16]));
		}

		if (shader.uniforms().containsKey("u_Modulate")) {
			glUniform3f(shader.uniforms().get("u_Modulate"), material.modulate.x, material.modulate.y, material.modulate.z);
		}
	}

	public void draw(VertexArray vertexArray, IndexBuffer indexBuffer, AbstractMaterial material) {
		material.bind();
		material.update();

		updateCommonUniforms(material);

		vertexArray.bind();
		indexBuffer.bind();

		// DRAW CALL
		glDrawElements(GL_TRIANGLES, indexBuffer.count(), indexBuffer.type(), NULL);
	}
}
