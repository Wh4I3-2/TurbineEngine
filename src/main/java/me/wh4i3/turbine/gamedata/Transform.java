package me.wh4i3.turbine.gamedata;

import org.joml.Vector2f;

public class Transform {
	public Vector2f position;
	public Vector2f scale;
	public float rotation;

	public Transform(Vector2f position, Vector2f scale, float rotation) {
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}

	public Transform(Vector2f position, Vector2f scale) {
		this(position, scale, 0.0f);
	}

	public Transform(Vector2f position) {
		this(position, new Vector2f(1), 0.0f);
	}

	public Transform(Vector2f position, float rotation) {
		this(position, new Vector2f(1), rotation);
	}

	public Transform() {
		this(new Vector2f(0), new Vector2f(1), 0.0f);
	}

	public Transform(Transform transform) {
		this(new Vector2f(transform.position), new Vector2f(transform.scale), transform.rotation);
	}

	public Transform add(Transform transform) {
		this.position.add(transform.position);
		this.scale.mul(transform.scale);
		this.rotation += transform.rotation;
		return this;
	}
}
