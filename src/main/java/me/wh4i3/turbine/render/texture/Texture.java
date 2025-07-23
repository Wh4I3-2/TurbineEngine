package me.wh4i3.turbine.render.texture;

import me.wh4i3.turbine.resource.ResourceKey;
import me.wh4i3.turbine.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL45C.*;

public class Texture {
	private final int rendererID;
	private final int width;
	private final int height;
	private BufferedImage image;

	private final ResourceKey key;

	public Texture(ResourceKey key) {
		this(key, GL_LINEAR, GL_CLAMP_TO_EDGE);
	}

	public Texture(ResourceKey key, int filter) {
		this(key, filter, GL_CLAMP_TO_EDGE);
	}

	public Texture(ResourceKey key, int filter, int wrap) {
		this.key = key;

		try {
			this.image = ImageUtils.createFlipped(ImageIO.read(key.withPrefix("assets/textures/").openStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.width = this.image.getWidth();
		this.height = this.image.getHeight();

		this.rendererID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.rendererID);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);


		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, ImageUtils.imageToBytes(image));
		glBindTexture(GL_TEXTURE_2D, 0);

		image.flush();
	}

	public Texture delete() {
		glDeleteTextures(this.rendererID);
		return null;
	}

	public int width() {
		return this.width;
	}
	public int height() {
		return this.height;
	}

	public void bind() {
		this.bind(0);
	}

	public void bind(int slot) {
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, this.rendererID);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
