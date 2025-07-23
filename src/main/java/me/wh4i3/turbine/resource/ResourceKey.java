package me.wh4i3.turbine.resource;

import me.wh4i3.turbine.Main;

import java.io.InputStream;

public class ResourceKey {
	private final String namespace;
	private final String path;

	public ResourceKey(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}

	public ResourceKey(String path) {
		this("", path);
	}

	public String namespace() {
		return this.namespace;
	}

	public String path() {
		return this.path;
	}

	public ResourceKey withPrefix(String prefix) {
		return new ResourceKey(this.namespace, prefix + this.path);
	}

	public ResourceKey withSuffix(String suffix) {
		return new ResourceKey(this.namespace, this.path + suffix);
	}

	public InputStream openStream() {
		return ResourceKey.class.getClassLoader().getResourceAsStream(this.namespace() + "/" + this.path());
	}

	public static ResourceKey withDefaultNamespace(String path) {
		return new ResourceKey("turbine", path);
	}

	public String toString() {
		return  this.namespace + ":" + this.path;
	}
}
