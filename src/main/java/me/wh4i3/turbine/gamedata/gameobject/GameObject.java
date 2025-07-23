package me.wh4i3.turbine.gamedata.gameobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GameObject {
	public int z;

	private List<GameObject> children;

	public List<GameObject> children() {
		return this.children;
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
			allChildren.addAll(go.childrenRecursive());
		}
		return allChildren;
	}
}
