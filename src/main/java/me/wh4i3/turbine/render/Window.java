package me.wh4i3.turbine.render;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45C.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
	public static final int VIEWPORT_WIDTH = 1920/6;
	public static final int VIEWPORT_HEIGHT = 1080/6;

	public static final int DEFAULT_WINDOW_WIDTH = 1920/3;
	public static final int DEFAULT_WINDOW_HEIGHT = 1080/3;

	private static Window current;

	private boolean fullscreen = false;

	GLCapabilities glCapabilities;

	// The window handle
	private long window;
	private int frameBuffer;
	private int textureBuffer;

	public int viewportWidth = VIEWPORT_WIDTH;
	public int viewportHeight = VIEWPORT_HEIGHT;
	public int viewportXOffset = 0;
	public int viewportYOffset = 0;

	public Window() {
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE);

		createWindow();

		current = this;
	}

	public void createWindow() {
		// Create the window
		window = glfwCreateWindow(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, "Turbine", fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);


		if (glCapabilities == null) {
			glCapabilities = GL.createCapabilities();
		}

		this.frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, this.frameBuffer);

		this.textureBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.textureBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, VIEWPORT_WIDTH + 2, VIEWPORT_HEIGHT + 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.textureBuffer, 0);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		glfwSetWindowSizeCallback(window, (win, width, height) -> {
			float windowAspect = (float) width / height;
			float targetAspect = (float) VIEWPORT_WIDTH / VIEWPORT_HEIGHT;

			if (windowAspect > targetAspect) {
				// Window is wider than target: add black bars on left/right
				viewportHeight = height;
				viewportWidth = (int)(viewportHeight * targetAspect);
				viewportXOffset = (width - viewportWidth) / 2;
				viewportYOffset = 0;
			} else {
				// Window is taller than target: add black bars on top/bottom
				viewportWidth = width;
				viewportHeight = (int)(viewportWidth / targetAspect);
				viewportXOffset = 0;
				viewportYOffset = (height - viewportHeight) / 2;
			}
		});

		glfwSetWindowSize(window, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	}

	public void terminate() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}

	private Vector2i windowPos;
	private Vector2i windowSize;

	public void toggleFullscreen() {
		fullscreen = !fullscreen;

		if (!fullscreen) {
			glfwSetWindowMonitor(window, NULL, windowPos.x, windowPos.y, windowSize.x, windowSize.y, 0);
			return;
		}

		int[] xPos = new int[1];
		int[] yPos = new int[1];
		int[] width = new int[1];
		int[] height = new int[1];

		glfwGetWindowPos(window, xPos, yPos);
		glfwGetWindowSize(window, width, height);

		windowPos = new Vector2i(xPos[0], yPos[0]);
		windowSize = new Vector2i(width[0], height[0]);

		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, mode.width(), mode.height(), mode.refreshRate());
	}

	public long window() {
		return this.window;
	}
	public int frameBuffer() {
		return this.frameBuffer;
	}
	public int textureBuffer() {
		return this.textureBuffer;
	}
	public GLCapabilities glCapabilities() {
		return this.glCapabilities;
	}
	public static Window current() {
		return current;
	}
}
