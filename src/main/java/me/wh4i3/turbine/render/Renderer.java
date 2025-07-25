package me.wh4i3.turbine.render;

import me.wh4i3.turbine.Time;
import me.wh4i3.turbine.gamedata.Transform;
import me.wh4i3.turbine.gamedata.gameobject.Camera;
import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.shader.Shader;
import me.wh4i3.turbine.render.shader.material.AbstractMaterial;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


import static org.lwjgl.opengl.GL45C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
	public enum Projection {
		ORTHOGRAPHIC,
		PERSPECTIVE,
	}

	private static Renderer instance;

	public static Renderer instance() {
		if (instance == null) {
			instance = new Renderer();
		}
		return instance;
	};

	private static Camera currentCamera;

	public Matrix4f orthoProjectionMatrix = new Matrix4f().ortho(
			0.0f, Window.VIEWPORT_WIDTH + 2.0f,
			0.0f, Window.VIEWPORT_HEIGHT + 2.0f,
			0.001f, 1000.0f
	);


	public Matrix4f perspectiveProjectionMatrix = new Matrix4f().perspective(90.0f, 16.0f/9.0f, 0.0001f, 1000.0f);

	public Matrix4f viewMatrix = new Matrix4f().identity();
	public Vector2f subPixelView = new Vector2f(0);

	public Matrix4f modelMatrix = new Matrix4f().identity();

	public Projection projection = Projection.ORTHOGRAPHIC;

	public Matrix4f projectionMatrix() {
		return switch (projection) {
			case Projection.ORTHOGRAPHIC -> orthoProjectionMatrix;
			case Projection.PERSPECTIVE -> perspectiveProjectionMatrix;
		};
	}

	public Matrix4f mvp() {
		return new Matrix4f(projectionMatrix()).mul(viewMatrix).mul(modelMatrix);
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	public void updateCommonUniforms(AbstractMaterial material) {
		Shader shader = material.shader();

		if (shader.uniforms().containsKey("u_Time")) {
			glUniform1f(shader.uniforms().get("u_Time"), Time.time());
		}

		if (shader.uniforms().containsKey("u_MVP")) {
			glUniformMatrix4fv(shader.uniforms().get("u_MVP"), false, mvp().get(new float[16]));
		}

		if (shader.uniforms().containsKey("u_Modulate")) {
			glUniform3f(shader.uniforms().get("u_Modulate"), material.modulate.x, material.modulate.y, material.modulate.z);
		}
	}

	public void update() {
		if (currentCamera != null) {
			Transform transform = new Transform(currentCamera.globalTransform());
			transform.position.sub(new Vector2f(Window.VIEWPORT_WIDTH, Window.VIEWPORT_HEIGHT).div(2));
			viewMatrix = new Matrix4f().identity().translate(new Vector3f(-transform.position.x, -transform.position.y, 0.0f).floor()).translate(0.0f, 0.0f, currentCamera.zoom);
			subPixelView = new Vector2f(-transform.position.x, -transform.position.y).sub(new Vector2f(-transform.position.x, -transform.position.y).floor());
		}
	}

	public void draw(VertexArray vertexArray, IndexBuffer indexBuffer, AbstractMaterial material, Transform transform) {
		modelMatrix = new Matrix4f().translate(transform.position.x, transform.position.y, 0.0f).scale(transform.scale.x, transform.scale.y, 1.0f).rotateX(transform.rotation);

		material.update();
		material.bind();

		updateCommonUniforms(material);

		vertexArray.bind();
		indexBuffer.bind();

		// DRAW CALL
		glDrawElements(GL_TRIANGLES, indexBuffer.count(), indexBuffer.type(), NULL);
	}

	public void draw(VertexArray vertexArray, IndexBuffer indexBuffer, AbstractMaterial material) {
		draw(vertexArray, indexBuffer, material, new Transform());
	}

	public void draw(RenderPrimitives.RenderPrimitive primitive, AbstractMaterial material) {
		draw(primitive.vertexArray, primitive.indexBuffer, material);
	}

	public void draw(RenderPrimitives.RenderPrimitive primitive, AbstractMaterial material, Transform transform) {
		draw(primitive.vertexArray, primitive.indexBuffer, material, transform);
	}

	public static Camera currentCamera() {
		return currentCamera;
	}

	public static void makeCameraCurrent(Camera camera) {
		currentCamera = camera;
	}
}
