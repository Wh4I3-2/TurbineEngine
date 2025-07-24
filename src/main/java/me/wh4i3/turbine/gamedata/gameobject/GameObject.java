package me.wh4i3.turbine.gamedata.gameobject;

import me.wh4i3.turbine.gamedata.Transform;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
	public int z;
	public Transform transform = new Transform();

	private final List<GameObject> children = new ArrayList<>();

	public List<GameObject> children() {
		return this.children;
	}

	public void addChild(GameObject go) {
		this.children.add(go);
		go.ready();
	}

	public void removeChild(GameObject go) {
		this.children.remove(go);
	}

	public void removeChild(int i) {
		this.children.remove(i);
	}

	public abstract void ready();
	public abstract void update();
	public abstract void physicsUpdate();
	public abstract void draw();

	public List<GameObject> childrenRecursive() {
		List<GameObject> allChildren = new ArrayList<>();
		if (this.children() == null) {
			return allChildren;
		}
		for (GameObject go : this.children()) {
			allChildren.add(go);
			allChildren.addAll(go.childrenRecursive());
		}
		return allChildren;
	}
	public int z() {
		return this.z;
	}
}
