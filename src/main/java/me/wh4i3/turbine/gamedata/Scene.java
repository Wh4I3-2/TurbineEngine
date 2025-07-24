package me.wh4i3.turbine.gamedata;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.gamedata.gameobject.GameObject;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
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
		List<GameObject> gos = gameObjects();
		gos.sort(Comparator.comparing(GameObject::z));
		for (GameObject go : gos) {
			go.draw();
		}
	}

	public GameObject root() {
		return this.root;
	}

	public List<GameObject> gameObjects() {
		List<GameObject> gameObjects = this.root.childrenRecursive();
		gameObjects.add(this.root);
		return gameObjects;
	}
}
