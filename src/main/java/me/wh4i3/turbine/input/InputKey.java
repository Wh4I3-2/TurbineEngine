package me.wh4i3.turbine.input;

import static org.lwjgl.glfw.GLFW.*;

public class InputKey {
	public final int key;
	public final int scancode;
	public final int mods;

	public InputKey(int key, int mods) {
		this.key = key;
		this.scancode = glfwGetKeyScancode(key);
		this.mods = mods;
	}

	public InputKey(int key) {
		this(key, 0);
	}
}
