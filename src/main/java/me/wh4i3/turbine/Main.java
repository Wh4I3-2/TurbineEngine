package me.wh4i3.turbine;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.wh4i3.turbine.gamedata.Scene;
import me.wh4i3.turbine.gamedata.gameobject.TileMap;
import me.wh4i3.turbine.input.Input;
import me.wh4i3.turbine.input.InputKey;
import me.wh4i3.turbine.input.event.InputScrollEvent;
import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.Buffer.VertexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexBufferLayout;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.Window;
import me.wh4i3.turbine.render.shader.material.ViewportMaterial;
import me.wh4i3.turbine.render.texture.AtlasTexture;
import me.wh4i3.turbine.gamedata.tilemap.Tile;
import me.wh4i3.turbine.resource.ResourceKey;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
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

	public static final int TPS = 20;
	public static long tick = 0;
	public static long tickStart;

	public static int frame = 0;
	public static int fpsFrame = 0;

	public static float deltaTime() {
		return (System.nanoTime() - tickStart) * 0.000000001f;
	}

	public static int fps;

	private static ScheduledExecutorService physicsExecutor;
	private static ScheduledExecutorService executor;

	public static void terminate() {

		instance().window.terminate();

		instance().imGuiGlfw.shutdown();
		instance().imGuiGl3.shutdown();
		ImGui.destroyContext();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();

		physicsExecutor.shutdownNow();
		executor.shutdownNow();
	}

	private ImGuiImplGlfw imGuiGlfw;
	private ImGuiImplGl3 imGuiGl3;

	private Window window;
	public Window window() {
		return this.window;
	}
	public Scene scene;

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

		instance().scene = new Scene(new TileMap() {
			private int tick = 0;
			private int x = 0;
			private Vector3f translation;

			@Override
			public void ready() {
				translation = new Vector3f(0.0f, 0.0f, -56.0f);

				this.z = -1;
				this.tileSize = new Vector2f(16.0f);
				this.tileSet.textures = List.of(
						new AtlasTexture(ResourceKey.withDefaultNamespace("sheet.png"), new Vector2i(16))
				);

				for (int y = 0; y < 100; y++) {
					int tileX = Math.round(new Random().nextFloat() * 15);
					int tileY = Math.round(new Random().nextFloat() * 15);
					this.setTile(new Vector2i(x, y), new Tile(0, new Vector2i(tileX, tileY)));
				}

				Input.addEventListener(InputScrollEvent.class, event -> {
					translation.z += (float)event.y;
				});
			}

			@Override
			public void update() {
				super.update();
				Vector2f input = Input.vector(new InputKey(GLFW_KEY_A), new InputKey(GLFW_KEY_D), new InputKey(GLFW_KEY_S), new InputKey(GLFW_KEY_W));

				if (input.length() != 0.0f) {
					input = input.normalize();
				}

				Vector2f moveVector = new Vector2f(input).mul(Input.pressed(new InputKey(GLFW_KEY_LEFT_SHIFT)) ? 1.5f : 0.5f);

				translation.add(moveVector.x, moveVector.y, 0.0f);
				Renderer.instance().viewMatrix = new Matrix4f().identity().translate(new Vector3f(translation).floor());

				if (tick % 2 == 0) {
					x++;
					for (int y = 0; y < 100; y++) {
						int tileX = Math.round(new Random().nextFloat() * 15);
						int tileY = Math.round(new Random().nextFloat() * 15);
						this.setTile(new Vector2i(x, y), new Tile(0, new Vector2i(tileX, tileY)));
					}
				}
			}

			@Override
			public void physicsUpdate() {
				tick++;
			}
		});

		instance().scene.ready();

		physicsExecutor = Executors.newScheduledThreadPool(1);
		physicsExecutor.scheduleAtFixedRate(() -> {
			tickStart = System.nanoTime();
			if (tick % TPS == 0) {
				fps = fpsFrame;
				fpsFrame = 0;
			}

			instance().scene.physicsUpdate();

			if (Input.justReleased(new InputKey(GLFW_KEY_F11, 0))) {
				instance().window.toggleFullscreen();
			}
			Input.instance().flush();

			tick++;
		}, 1, 1000 / TPS, TimeUnit.MILLISECONDS);

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

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			if (glfwWindowShouldClose(instance().window.window())) {
				terminate();
			}

			Main.frame++;
			Main.fpsFrame++;

			glBindFramebuffer(GL_FRAMEBUFFER, instance().window.frameBuffer());
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, instance().window.textureBuffer());

			glViewport(0, 0, Window.VIEWPORT_WIDTH, Window.VIEWPORT_HEIGHT);


			Renderer.instance().clear();
			instance().scene.update();
			instance().scene.draw();


			glBindFramebuffer(GL_FRAMEBUFFER, 0);

			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, instance().window.textureBuffer());

			glViewport(instance().window.viewportXOffset, instance().window.viewportYOffset, instance().window.viewportWidth, instance().window.viewportHeight);

			Renderer.instance().draw(viewportVA, viewportIB, viewportMaterial);

			// ImGui
			instance().imGuiGlfw.newFrame();
			instance().imGuiGl3.newFrame();
			ImGui.newFrame();

			ImGui.begin("Performance");
			ImGui.text(String.format("Global tick: %d, Local tick: %d/%d, Second: %.2f, DeltaTime: %.5f", Main.tick, Main.tick % Main.TPS, Main.TPS, (double) Main.tick / Main.TPS, Main.deltaTime()));
			ImGui.text(String.format("Frame: %d, FPS frame: %d, FPS: %d", Main.frame, Main.fpsFrame, Main.fps));
			ImGui.end();

			ImGui.begin("World");
			ImGui.text("View Matrix Translation");
			Vector3f viewMatrixTranslation = new Vector3f(0.0f);
			viewMatrixTranslation = Renderer.instance().viewMatrix.getTranslation(viewMatrixTranslation);
			ImGui.text(String.format("X: %.2f, Y: %.2f, Z: %.2f", viewMatrixTranslation.x, viewMatrixTranslation.y, viewMatrixTranslation.z));

			ImGui.text("Projection Matrix Translation");
			Vector3f projectionMatrixTranslation = new Vector3f(0.0f);
			projectionMatrixTranslation = Renderer.instance().projectionMatrix.getTranslation(projectionMatrixTranslation);
			ImGui.text(String.format("X: %.2f, Y: %.2f, Z: %.2f", projectionMatrixTranslation.x, projectionMatrixTranslation.y, projectionMatrixTranslation.z));
			ImGui.end();

			ImGui.render();
			instance().imGuiGl3.renderDrawData(ImGui.getDrawData());

			glfwSwapBuffers(instance().window.window()); // swap the color buffers
			glfwPollEvents();

			glfwMakeContextCurrent(0);
		}, 1, 1, TimeUnit.MILLISECONDS);
	}
}
