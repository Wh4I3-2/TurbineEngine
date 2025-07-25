package me.wh4i3.turbine.gamedata.gameobject;

import me.wh4i3.turbine.gamedata.Transform;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
	public int z;
	public Transform localTransform = new Transform();
	public Transform globalTransform() {
		if (owner == null) {
			return new Transform(localTransform);
		}
		return owner.globalTransform().add(localTransform);
	};

	public GameObject owner;

	private final List<GameObject> children = new ArrayList<>();

	public List<GameObject> children() {
		return this.children;
	}

	public GameObject addChild(GameObject go) {
		this.children.add(go);
		go.owner = this;
		go.ready();
		return go;
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
