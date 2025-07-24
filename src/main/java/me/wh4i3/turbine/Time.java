package me.wh4i3.turbine;

public class Time {

	public static final int TPS = 20;

	private static long tick;
	private static long lastTickTime;
	private static float deltaTick;

	private static long frame;
	private static long lastFrameTime;
	private static float deltaFrame;

	public static void updateTick() {
		long currentTime = System.nanoTime();
		deltaTick = (currentTime - lastTickTime) / 1_000_000_000f;
		lastTickTime = currentTime;
		tick++;
	}

	public static void updateFrame() {
		long currentTime = System.nanoTime();
		deltaFrame = (currentTime - lastFrameTime) / 1_000_000_000f;
		lastFrameTime = currentTime;
		frame++;
	}

	public static float tps() {
		return 1.0f / deltaTick;
	}
	public static float fps() {
		return 1.0f / deltaFrame;
	}

	public static long tick() {
		return tick;
	}

	public static long frame() {
		return frame;
	}

	public static float deltaTime() {
		return Thread.currentThread().getName().equals("main") ? deltaFrame : deltaTick;
	}

	public static float deltaFrame() {
		return deltaFrame;
	}

	public static float deltaTick() {
		return deltaTick;
	}

	public static float time() {
		return (float) tick / TPS + deltaTime();
	}
}
