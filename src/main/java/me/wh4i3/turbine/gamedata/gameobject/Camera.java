package me.wh4i3.turbine.gamedata.gameobject;

import me.wh4i3.turbine.render.Renderer;

public abstract class Camera extends GameObject {
	public float zoom = 0.0f;

	public void makeCurrent() {
		Renderer.makeCameraCurrent(this);
	}

	@Override
	public void draw() {
	}
}
