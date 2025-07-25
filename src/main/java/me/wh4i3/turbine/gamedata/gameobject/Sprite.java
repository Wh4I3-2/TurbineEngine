package me.wh4i3.turbine.gamedata.gameobject;

import me.wh4i3.turbine.gamedata.Transform;
import me.wh4i3.turbine.render.RenderPrimitives;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.shader.material.TextureMaterial;
import me.wh4i3.turbine.render.texture.Texture;

public abstract class Sprite extends GameObject {
	public Texture texture;

	@Override
	public void draw() {
		Transform transform = new Transform(globalTransform());
		transform.scale.x *= texture.width();
		transform.scale.y *= texture.height();
		transform.position.x -= transform.scale.x / 2.0f;
		transform.position.y -= transform.scale.y / 2.0f;
		Renderer.instance().draw(RenderPrimitives.TEXTURE_QUAD, new TextureMaterial(texture), transform);
	}
}
