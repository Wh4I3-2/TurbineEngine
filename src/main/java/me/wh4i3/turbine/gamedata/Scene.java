package me.wh4i3.turbine.gamedata;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.gamedata.gameobject.GameObject;
import me.wh4i3.turbine.render.Renderer;

import java.util.List;
import java.util.logging.Logger;

public class Scene {
	private GameObject root;

	public Scene(GameObject root) {
		this.root = root;
	}

	public void ready() {
		for (GameObject go : gameObjects()) {
			go.ready();
		}
	}

	public void update() {
		for (GameObject go : gameObjects()) {
			go.update();
		}
	}

	public void physicsUpdate() {
		for (GameObject go : gameObjects()) {
			go.physicsUpdate();
		}
	}

	public void draw() {
		for (GameObject go : gameObjects()) {
			go.draw();
		}
	}

	public GameObject root() {
		return this.root;
	}

	public List<GameObject> gameObjects() {
		List<GameObject> gameObjects = root.childrenRecursive();
		gameObjects.addFirst(this.root);
		return gameObjects;
	}
}
