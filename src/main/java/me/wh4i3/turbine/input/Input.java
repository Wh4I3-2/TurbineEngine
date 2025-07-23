package me.wh4i3.turbine.input;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.event.Event;
import me.wh4i3.turbine.event.EventHandler;
import me.wh4i3.turbine.event.EventListener;
import me.wh4i3.turbine.input.event.InputEvent;
import me.wh4i3.turbine.input.event.InputScrollEvent;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	private static Input instance;

	public static Input instance() {
		if (instance == null) {
			instance = new Input();
		}
		return instance;
	}

	private Map<Integer, InputKey> inputKeyStack;
	private Map<Integer, InputKey> justPressedKeyStack;
	private Map<Integer, InputKey> justReleasedKeyStack;

	private Map<Class<? extends Event>, EventHandler<?>> handlers;

	private Input() {
		this.inputKeyStack = new HashMap<>();
		this.justPressedKeyStack = new HashMap<>();
		this.justReleasedKeyStack = new HashMap<>();

		this.handlers = new HashMap<>();
		this.handlers.put(InputScrollEvent.class, new EventHandler<InputScrollEvent>());
		this.handlers.put(InputEvent.class, new EventHandler<InputEvent>());

		glfwSetKeyCallback(Main.instance().window().window(), (window, key, scancode, action, mods) -> {
			getEventHandler(InputEvent.class).call(new InputEvent(new InputKey(key, mods), action));
			switch (action) {
				case GLFW_PRESS -> {
					if (!this.inputKeyStack.containsKey(key)) {
						this.justPressedKeyStack.put(key, new InputKey(key, mods));
					}
					this.inputKeyStack.put(key, new InputKey(key, mods));
				}
				case GLFW_RELEASE -> {
					if (this.inputKeyStack.containsKey(key)) {
						this.justReleasedKeyStack.put(key, new InputKey(key, mods));
					}
					this.inputKeyStack.remove(key);
				}
			}
		});

		glfwSetScrollCallback(Main.instance().window().window(), (window, xOffset, yOffset) -> {
			getEventHandler(InputScrollEvent.class).call(new InputScrollEvent(xOffset, yOffset));
		});
	}

	private <T extends Event> EventHandler<T> getEventHandler(Class<T> event) {
		return (EventHandler<T>) this.handlers.get(event);
	}

	private void flushPressed() {
		this.justPressedKeyStack.clear();
	}

	private void flushReleased() {
		this.justReleasedKeyStack.clear();
	}

	public void flush() {
		flushPressed();
		flushReleased();
	}

	public static <T extends Event> void addEventListener(Class<T> event, EventListener<T> listener) {
		EventHandler<?> genericHandler = instance().handlers.get(event);
		if (genericHandler == null) {
			return;
		}
		try {
			EventHandler<T> handler = (EventHandler<T>) genericHandler;
			handler.addListener(listener);
		} catch (Exception ignored) {}
	}

	public static <T extends Event> void removeEventListener(Class<T> event, EventListener<T> listener) {
		EventHandler<?> genericHandler = instance().handlers.get(event);
		if (genericHandler == null) {
			return;
		}
		try {
			EventHandler<T> handler = (EventHandler<T>) genericHandler;
			handler.removeListener(listener);
		} catch (Exception ignored) {}
	}

	public static boolean justPressed(InputKey key) {
		return instance().justPressedKeyStack.containsKey(key.key);
	}

	public static boolean justReleased(InputKey key) {
		return instance().justReleasedKeyStack.containsKey(key.key);
	}

	public static boolean pressed(InputKey key) {
		return instance().inputKeyStack.containsKey(key.key);
	}

	public static float axis(InputKey negative, InputKey positive) {
		float negativePressed = pressed(negative) ? 1.0f : 0.0f;
		float positivePressed = pressed(positive) ? 1.0f : 0.0f;

		return negativePressed - positivePressed;
	}

	public static Vector2f vector(InputKey negativeX, InputKey positiveX, InputKey negativeY, InputKey positiveY) {
		float x = axis(negativeX, positiveX);
		float y = axis(negativeY, positiveY);

		return new Vector2f(x, y);
	}
}
