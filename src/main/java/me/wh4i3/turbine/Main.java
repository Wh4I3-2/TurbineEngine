package me.wh4i3.turbine;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.wh4i3.turbine.game.InfiniteTileMap;
import me.wh4i3.turbine.gamedata.Scene;
import me.wh4i3.turbine.gamedata.gameobject.GameObject;
import me.wh4i3.turbine.input.Input;
import me.wh4i3.turbine.input.InputKey;
import me.wh4i3.turbine.input.event.InputEvent;
import me.wh4i3.turbine.input.event.InputScrollEvent;
import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.Buffer.VertexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexBufferLayout;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.Window;
import me.wh4i3.turbine.render.shader.material.ViewportMaterial;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45C.*;

public class Main {
	public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static Main instance;

	public static Main instance() {
		if (instance == null) {
			instance = new Main();
		}

		return instance;
	}
	public static void terminate() {

		instance().window.terminate();

		instance().imGuiGlfw.shutdown();
		instance().imGuiGl3.shutdown();
		ImGui.destroyContext();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();

		physicsExecutor.shutdownNow();
	}

	private static ScheduledExecutorService physicsExecutor;

	private ImGuiImplGlfw imGuiGlfw;
	private ImGuiImplGl3 imGuiGl3;

	private Window window;
	public Window window() {
		return this.window;
	}
	public Scene scene;

	public static boolean debugEnabled = false;

	public static final Queue<Runnable> glQueue = new ConcurrentLinkedQueue<>();

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback((error, description) -> {
			LOGGER.error(MemoryUtil.memASCII(description));
		});

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		window = new Window();
	}

	public static void main(String[] args) {
		instance().init();

		instance().scene = new Scene(new GameObject() {
			private Vector2f target;
			private Vector3f current;

			@Override
			public void ready() {
				target = new Vector2f(0);
				current = new Vector3f(0.0f, 0.0f, -56.0f);

				Input.addEventListener(InputScrollEvent.class, event -> {
					current.z += (float)event.y * 2.0f;
				});

				Input.addEventListener(InputEvent.class, event -> {
					if (event.key.key == GLFW_KEY_F3 && event.action == GLFW_RELEASE) {
						debugEnabled = !debugEnabled;
						return;
					}
					if (event.key.key == GLFW_KEY_F11 && event.action == GLFW_RELEASE) {
						instance().window.toggleFullscreen();
					}
				});

				this.addChild(new InfiniteTileMap(50, new Vector3f(0.0f)));
				this.addChild(new InfiniteTileMap(35, new Vector3f(0.2f)));
				this.addChild(new InfiniteTileMap(0, new Vector3f(1)));
				this.addChild(new InfiniteTileMap(-25, new Vector3f(0.8625f)));
				this.addChild(new InfiniteTileMap(-50, new Vector3f(0.75f)));
				this.addChild(new InfiniteTileMap(-75, new Vector3f(0.6125f)));
				this.addChild(new InfiniteTileMap(-100, new Vector3f(0.5f)));
			}

			@Override
			public void update() {
				Vector2f input = Input.vector(new InputKey(GLFW_KEY_A), new InputKey(GLFW_KEY_D), new InputKey(GLFW_KEY_S), new InputKey(GLFW_KEY_W));

				if (input.length() != 0.0f) {
					input = input.normalize();
				}

				float delta = Time.deltaTime();

				float speed = Input.pressed(new InputKey(GLFW_KEY_LEFT_SHIFT)) ? 400.0f : 100.0f;

				Vector2f moveVector = new Vector2f(input);
				moveVector.mul(delta * speed);

				target.add(moveVector.x, moveVector.y);

				current.x = Math.lerp(current.x, Math.floor(target.x), delta * 3.0f);
				current.y = Math.lerp(current.y, Math.floor(target.y), delta * 3.0f);

				Renderer.instance().subPixelView = new Vector2f(current.x, current.y).sub(new Vector2f(current.x, current.y).floor());
				Renderer.instance().viewMatrix = new Matrix4f().identity().translate(new Vector3f(current).floor());
			}

			@Override
			public void physicsUpdate() {
			}

			@Override
			public void draw() {

			}
		});

		instance().scene.ready();

		physicsExecutor = Executors.newScheduledThreadPool(1);
		physicsExecutor.scheduleAtFixedRate(() -> {
			instance().scene.physicsUpdate();

			Input.instance().flush();

			Time.updateTick();
		}, 1, 1000 / Time.TPS, TimeUnit.MILLISECONDS);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glUseProgram(0);
		glBindVertexArray(0);

		ImGui.createContext();
		instance().imGuiGlfw = new ImGuiImplGlfw();
		instance().imGuiGl3 = new ImGuiImplGl3();
		instance().imGuiGlfw.init(instance().window.window(), true);
		instance().imGuiGl3.init("#version 450 core");

		ImGui.styleColorsDark();

		final VertexArray viewportVA = new VertexArray();
		final VertexBuffer viewportVB = new VertexBuffer(new float[] {
				-1.0f, -1.0f, 0.0f, 0.0f,
				 1.0f, -1.0f, 1.0f, 0.0f,
				 1.0f,  1.0f, 1.0f, 1.0f,
				-1.0f,  1.0f, 0.0f, 1.0f,
		});
		final VertexBufferLayout viewportVBL = new VertexBufferLayout();
		viewportVBL.pushFloat(2);
		viewportVBL.pushFloat(2);

		viewportVA.addBuffer(viewportVB, viewportVBL);

		final IndexBuffer viewportIB = new IndexBuffer(new int[] {
				0, 1, 2,
				2, 3, 0,
		});

		final ViewportMaterial viewportMaterial = new ViewportMaterial();

		LOGGER.info(
				Thread.currentThread().getName());

		while (!glfwWindowShouldClose(instance().window.window())){
			Time.updateFrame();

			glBindFramebuffer(GL_FRAMEBUFFER, instance().window.frameBuffer());
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, instance().window.textureBuffer());

			glViewport(0, 0, Window.VIEWPORT_WIDTH + 2, Window.VIEWPORT_HEIGHT + 2);

			while (!glQueue.isEmpty()) {
				glQueue.poll().run(); // Run GL-safe actions
			}

			Renderer.instance().clear();
			instance().scene.update();
			instance().scene.draw();


			glBindFramebuffer(GL_FRAMEBUFFER, 0);

			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, instance().window.textureBuffer());

			glViewport(instance().window.viewportXOffset, instance().window.viewportYOffset, instance().window.viewportWidth, instance().window.viewportHeight);

			Renderer.instance().draw(viewportVA, viewportIB, viewportMaterial);

			if (debugEnabled) {
				// ImGui
				instance().imGuiGlfw.newFrame();
				instance().imGuiGl3.newFrame();
				ImGui.newFrame();

				ImGui.begin("Performance");

				ImGui.text("Frame:");
				ImGui.text(String.format("frame: %d, deltaFrame: %.6f, FPS: %.2f", Time.frame(), Time.deltaFrame(), Time.fps()));

				ImGui.newLine();

				ImGui.text("Tick:");
				ImGui.text(String.format("tick: %d, deltaTick: %.6f, TPS: %.2f", Time.tick(), Time.deltaTick(), Time.tps()));

				ImGui.end();

				ImGui.begin("Camera");

				ImGui.text(String.format("Projection: %s", Renderer.instance().projection.name()));
				if (ImGui.button("Toggle Projection")) {
					Renderer.instance().projection = Renderer.instance().projection.equals(Renderer.Projection.ORTHOGRAPHIC) ? Renderer.Projection.PERSPECTIVE : Renderer.Projection.ORTHOGRAPHIC;
				}

				ImGui.newLine();

				ImGui.text("View Matrix Translation");
				Vector3f viewMatrixTranslation = new Vector3f(0.0f);
				viewMatrixTranslation = Renderer.instance().viewMatrix.getTranslation(viewMatrixTranslation);
				ImGui.text(String.format("X: %.2f, Y: %.2f, Z: %.2f", viewMatrixTranslation.x, viewMatrixTranslation.y, viewMatrixTranslation.z));

				ImGui.text("Sub Pixel Position");
				ImGui.text(String.format("X: %.2f, Y: %.2f", Renderer.instance().subPixelView.x, Renderer.instance().subPixelView.y));

				ImGui.newLine();

				ImGui.text("Projection Matrix Translation");
				Vector3f projectionMatrixTranslation = new Vector3f(0.0f);
				projectionMatrixTranslation = Renderer.instance().projectionMatrix().getTranslation(projectionMatrixTranslation);
				ImGui.text(String.format("X: %.2f, Y: %.2f, Z: %.2f", projectionMatrixTranslation.x, projectionMatrixTranslation.y, projectionMatrixTranslation.z));

				ImGui.end();

				ImGui.render();
				instance().imGuiGl3.renderDrawData(ImGui.getDrawData());
			}


			glfwSwapBuffers(instance().window.window()); // swap the color buffers
			glfwPollEvents();
		}

		terminate();
	}
}
